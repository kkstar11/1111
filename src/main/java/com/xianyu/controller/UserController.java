package com.xianyu.controller;

import com.xianyu.dto.LoginDTO;
import com.xianyu.dto.RegisterDTO;
import com.xianyu.service.UserService;
import com.xianyu.util.Result;
import com.xianyu.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        try {
            return Result.success(userService.register(dto));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginDTO dto, HttpSession session) {
        Optional<UserVO> user = userService.login(dto);
        user.ifPresent(u -> session.setAttribute("currentUser", u));
        return user.map(Result::success).orElseGet(() -> Result.failure("invalid credentials or disabled"));
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("user not found"));
    }
}

