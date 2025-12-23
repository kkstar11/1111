// Java
package com.xianyu.dao;

import com.xianyu.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FavoriteMapper {

    int insert(Favorite favorite);

    int delete(@Param("userId") Long userId, @Param("itemId") Long itemId);

    Optional<Favorite> find(@Param("userId") Long userId, @Param("itemId") Long itemId);

    List<Favorite> listByUser(@Param("userId") Long userId);
}

