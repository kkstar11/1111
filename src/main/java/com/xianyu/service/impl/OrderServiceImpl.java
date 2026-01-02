package com.xianyu.service.impl;

import com.xianyu.dao.ItemMapper;
import com.xianyu.dao.OrderMapper;
import com.xianyu.dao.UserMapper;
import com.xianyu.dto.OrderCreateDTO;
import com.xianyu.entity.Item;
import com.xianyu.entity.Order;
import com.xianyu.entity.User;
import com.xianyu.service.OrderService;
import com.xianyu.vo.OrderVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public OrderServiceImpl(OrderMapper orderMapper, ItemMapper itemMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    @Override
    public OrderVO createOrder(OrderCreateDTO dto, Long buyerId) {
        if (dto == null || dto.getItemId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (buyerId == null) {
            throw new IllegalArgumentException("买家ID不能为空");
        }

        Item item = itemMapper.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("商品未找到"));

        if (item.getSellerId().equals(buyerId)) {
            throw new IllegalArgumentException("不能购买自己的商品");
        }

        Order order = new Order();
        order.setItemId(item.getId());
        order.setBuyerId(buyerId);
        order.setSellerId(item.getSellerId());
        order.setItemPrice(item.getPrice());
        order.setStatus(0);
        orderMapper.insert(order);

        return orderMapper.findById(order.getId()).map(this::toVO).orElseGet(() -> toVO(order));
    }

    @Override
    public Optional<OrderVO> finishOrder(Long orderId, Long userId) {
        Order order = orderMapper.findById(orderId).orElse(null);
        if (order == null) {
            return Optional.empty();
        }

        if (!order.getSellerId().equals(userId) && !order.getBuyerId().equals(userId)) {
            return Optional.empty();
        }

        if (order.getStatus() != 0) {
            return Optional.empty();
        }

        order.setStatus(1);
        order.setFinishTime(LocalDateTime.now());
        orderMapper.update(order);

        return orderMapper.findById(orderId).map(this::toVO);
    }

    @Override
    public Optional<OrderVO> cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.findById(orderId).orElse(null);
        if (order == null) {
            return Optional.empty();
        }

        if (!order.getSellerId().equals(userId) && !order.getBuyerId().equals(userId)) {
            return Optional.empty();
        }

        if (order.getStatus() != 0) {
            return Optional.empty();
        }

        order.setStatus(2);
        order.setFinishTime(LocalDateTime.now());
        orderMapper.update(order);

        return orderMapper.findById(orderId).map(this::toVO);
    }

    @Override
    public Optional<OrderVO> findById(Long orderId) {
        return orderMapper.findById(orderId).map(this::toVO);
    }

    @Override
    public List<OrderVO> listByBuyer(Long buyerId) {
        return orderMapper.findByBuyer(buyerId).stream().map(this::toVO).toList();
    }

    @Override
    public List<OrderVO> listBySeller(Long sellerId) {
        return orderMapper.findBySeller(sellerId).stream().map(this::toVO).toList();
    }

    private OrderVO toVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setItemId(order.getItemId());
        vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId());
        vo.setItemPrice(order.getItemPrice());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());
        vo.setFinishTime(order.getFinishTime());

        itemMapper.findById(order.getItemId()).ifPresent(item -> {
            vo.setItemName(item.getTitle());
        });

        userMapper.findById(order.getBuyerId()).ifPresent(buyer -> {
            vo.setBuyerName(buyer.getUsername());
        });

        userMapper.findById(order.getSellerId()).ifPresent(seller -> {
            vo.setSellerName(seller.getUsername());
        });

        return vo;
    }
}
