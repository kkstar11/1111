package com.xianyu.service.impl;

import com.xianyu.dto.LoginDTO;
import com.xianyu.dto.RegisterDTO;
import com.xianyu.dao.UserMapper;
import com.xianyu.entity.User;
import com.xianyu.service.UserService;
import com.xianyu.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserVO register(RegisterDTO dto) {
        validateRegister(dto);

        // 唯一校验
        userMapper.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new IllegalStateException("username exists");
        });
        userMapper.findByStudentId(dto.getUsername()).ifPresent(u -> {
            throw new IllegalStateException("studentId exists");
        });

        User user = new User();
        user.setStudentId(dto.getUsername()); // 简化：用 username 当学号，若有单独字段可调整
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setUserStatus(1);
        user.setUserRole(0);

        userMapper.insert(user);

        return toVO(user);
    }

    @Override
    public Optional<UserVO> login(LoginDTO dto) {
        if (dto == null || isBlank(dto.getUsername()) || isBlank(dto.getPassword())) {
            return Optional.empty();
        }
        User user = userMapper.findByUsername(dto.getUsername()).orElse(null);
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Optional.empty();
        }
        if (user.getUserStatus() != null && user.getUserStatus() == 0) {
            return Optional.empty();
        }
        return Optional.of(toVO(user));
    }

    @Override
    public Optional<UserVO> findById(Long id) {
        return userMapper.findById(id).map(this::toVO);
    }

    @Override
    public Optional<User> findEntityByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        return vo;
    }

    private void validateRegister(RegisterDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("payload required");
        }
        if (isBlank(dto.getUsername())) {
            throw new IllegalArgumentException("username required");
        }
        if (isBlank(dto.getPassword())) {
            throw new IllegalArgumentException("password required");
        }
        if (!isBlank(dto.getEmail()) && !EMAIL.matcher(dto.getEmail()).matches()) {
            throw new IllegalArgumentException("email invalid");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static final Pattern EMAIL = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
}

