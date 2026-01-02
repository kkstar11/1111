package com.xianyu.controller;

import com.xianyu.dto.RegisterDTO;
import com.xianyu.security.MyUserDetails;
import com.xianyu.util.Result;
import com.xianyu.vo.UserVO;
import com.xianyu.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        try {
            UserVO user = userService.register(dto);
            return Result.success(user);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();                // 清除Spring Security认证信息
        return Result.success(null);
    }
    @GetMapping("/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("用户未找到"));
    }
}