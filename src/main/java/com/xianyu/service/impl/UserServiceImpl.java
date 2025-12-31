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
    public Optional<UserVO> login(LoginDTO dto) {
        if (dto == null || isBlank(dto.getUsername()) || isBlank(dto.getPassword())) {
            System.out.println("[LOGIN] 空用户名或密码，拒绝登录");
            return Optional.empty();
        }
        User user = userMapper.findByUsername(dto.getUsername()).orElse(null);
        System.out.println("[LOGIN] 尝试登录，输入用户名: " + dto.getUsername() + ", 密码(明文): " + dto.getPassword());
        if (user == null) {
            System.out.println("[LOGIN] 用户未找到");
            return Optional.empty();
        }
        System.out.println("[LOGIN] 数据库密码(BCrypt): " + user.getPassword());
        boolean matches = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        System.out.println("[LOGIN] passwordEncoder.matches(输入明文, 数据库哈希): " + matches);
        if (!matches) {
            System.out.println("[LOGIN] 密码不匹配！");
            return Optional.empty();
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            System.out.println("[LOGIN] 用户被禁用！");
            return Optional.empty();
        }
        System.out.println("[LOGIN] 登录成功！");
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