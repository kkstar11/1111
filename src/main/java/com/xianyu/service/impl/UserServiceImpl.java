package com.xianyu.service.impl;

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
            throw new IllegalStateException("用户名已存在");
        });
        userMapper.findByStudentId(dto.getUsername()).ifPresent(u -> {
            throw new IllegalStateException("学号已存在");
        });

        User user = new User();
        user.setStudentId(dto.getUsername()); // 简化：用 username 当学号，若有单独字段可调整
        user.setUsername(dto.getUsername());
        String encodedPwd = passwordEncoder.encode(dto.getPassword());
        System.out.println("[REGISTER] 原始密码: " + dto.getPassword() + " 加密后: " + encodedPwd);
        user.setPassword(encodedPwd);
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        user.setRole(0);

        userMapper.insert(user);

        return toVO(user);
    }

    @Override
    public Optional<UserVO> findById(Long id) {
        return userMapper.findById(id).map(this::toVO);
    }

    @Override
    public Optional<User> findEntityByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public Optional<UserVO> findByUsername(String username) {
        return userMapper.findByUsername(username).map(this::toVO);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPassword(user.getPassword());
        vo.setRole(user.getRole());  // 添加角色字段映射
        vo.setStatus(user.getStatus());  // 添加状态字段映射
        // 有需要可拷贝更多字段
        return vo;
    }

    private void validateRegister(RegisterDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求数据不能为空");
        }
        if (isBlank(dto.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (isBlank(dto.getPassword())) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (!isBlank(dto.getEmail()) && !EMAIL.matcher(dto.getEmail()).matches()) {
            throw new IllegalArgumentException("邮箱格式无效");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static final Pattern EMAIL = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
}