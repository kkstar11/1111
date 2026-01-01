package com.xianyu.controller;

import com.xianyu.dto.LoginDTO;
import com.xianyu.dto.RegisterDTO;
import com.xianyu.security.MyUserDetails;
import com.xianyu.util.Result;
import com.xianyu.vo.UserVO;
import com.xianyu.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        try {
            UserVO user = userService.register(dto);
            System.out.println("[REGISTER] 注册新用户: " + user);
            return Result.success(user);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[REGISTER] 注册异常: " + e.getMessage());
            return Result.failure(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginDTO dto, HttpServletRequest request) { // ← 加 HttpServletRequest
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 【关键，添加此行手动同步SecurityContext到Session】
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            // ↑↑↑ 必须要有，否则商品接口即使同JSESSIONID也拿不到已认证用户！ ↑↑↑

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            System.out.println("[LOGIN] 登录成功: " + userDetails.getUserVO());
            
            // Set a flag in the result to indicate if user is admin (for frontend redirect)
            UserVO userVO = userDetails.getUserVO();
            return Result.success(userVO);
        } catch (Exception e) {
            System.out.println("[LOGIN] 登录失败: " + e.getMessage());
            return Result.failure("invalid credentials or disabled");
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();                // 清除Spring Security认证信息
        System.out.println("[LOGOUT] 用户已注销");
        return Result.success(null);
    }
    @GetMapping("/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        System.out.println("[GET USER] userId: " + id);
        return userService.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("user not found"));
    }
}