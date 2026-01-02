package com.xianyu.service;

import com.xianyu.dto.RegisterDTO;
import com.xianyu.entity.User;
import com.xianyu.vo.UserVO;

import java.util.Optional;

public interface UserService {

    UserVO register(RegisterDTO dto);

    Optional<UserVO> findById(Long id);

    Optional<User> findEntityByUsername(String username);  // 正常保留即可

    // 删除多余的 ScopedValue<T> findByUsername
    Optional<UserVO> findByUsername(String username);
}