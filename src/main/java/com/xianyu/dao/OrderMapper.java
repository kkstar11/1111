package com.xianyu.dao;

import com.xianyu.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {

    int insert(Order order);

    int update(Order order);

    Optional<Order> findById(@Param("id") Long id);

    List<Order> findByBuyer(@Param("buyerId") Long buyerId);

    List<Order> findBySeller(@Param("sellerId") Long sellerId);
}
