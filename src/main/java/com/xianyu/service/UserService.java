package com.xianyu.service;

import com.xianyu.dto.LoginDTO;
import com.xianyu.dto.RegisterDTO;
import com.xianyu.entity.User;
import com.xianyu.vo.UserVO;

import java.util.Optional;

public interface UserService {

    UserVO register(RegisterDTO dto);

    Optional<UserVO> login(LoginDTO dto);

    Optional<UserVO> findById(Long id);

    Optional<User> findEntityByUsername(String username);
}

