package com.xianyu.dao;

import com.xianyu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    int insert(User user);

    Optional<User> findById(@Param("id") Long id);

    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByStudentId(@Param("studentId") String studentId);

    List<User> findAll();

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}

