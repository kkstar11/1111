package com.xianyu.service;

import com.xianyu.dto.OrderCreateDTO;
import com.xianyu.vo.OrderVO;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderVO createOrder(OrderCreateDTO dto, Long buyerId);

    Optional<OrderVO> finishOrder(Long orderId, Long userId);

    Optional<OrderVO> cancelOrder(Long orderId, Long userId);

    Optional<OrderVO> findById(Long orderId);

    List<OrderVO> listByBuyer(Long buyerId);

    List<OrderVO> listBySeller(Long sellerId);
}
