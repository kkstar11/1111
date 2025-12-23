package com.xianyu.dao;

import com.xianyu.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {

    int insert(Item item);

    int update(Item item);

    int delete(@Param("id") Long id);

    Optional<Item> findById(@Param("id") Long id);

    List<Item> findAll();

    List<Item> findBySeller(@Param("sellerId") Long sellerId);

    List<Item> findByCategory(@Param("category") String category);
}

