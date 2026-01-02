package com.xianyu.controller;

import com.xianyu.dto.OrderCreateDTO;
import com.xianyu.service.OrderService;
import com.xianyu.util.Result;
import com.xianyu.vo.OrderVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<OrderVO> create(@RequestBody OrderCreateDTO dto, HttpSession session) {
        Long buyerId = currentUserId(session);
        if (buyerId == null) {
            return Result.failure("未授权");
        }
        try {
            return Result.success(orderService.createOrder(dto, buyerId));
        } catch (IllegalArgumentException e) {
            return Result.failure(e.getMessage());
        }
    }

    @PutMapping("/{id}/finish")
    public Result<OrderVO> finish(@PathVariable Long id, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return Result.failure("未授权");
        }
        Optional<OrderVO> result = orderService.finishOrder(id, userId);
        return result.map(Result::success).orElseGet(() -> Result.failure("未找到或无权限"));
    }

    @PutMapping("/{id}/cancel")
    public Result<OrderVO> cancel(@PathVariable Long id, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return Result.failure("未授权");
        }
        Optional<OrderVO> result = orderService.cancelOrder(id, userId);
        return result.map(Result::success).orElseGet(() -> Result.failure("未找到或无权限"));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> get(@PathVariable Long id, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return Result.failure("未授权");
        }
        return orderService.findById(id)
                .map(order -> {
                    if (order.getBuyerId().equals(userId) || order.getSellerId().equals(userId)) {
                        return Result.success(order);
                    }
                    return Result.<OrderVO>failure("无权限");
                })
                .orElseGet(() -> Result.failure("订单未找到"));
    }

    @GetMapping("/buyer")
    public Result<List<OrderVO>> listByBuyer(HttpSession session) {
        Long buyerId = currentUserId(session);
        if (buyerId == null) {
            return Result.failure("未授权");
        }
        return Result.success(orderService.listByBuyer(buyerId));
    }

    @GetMapping("/seller")
    public Result<List<OrderVO>> listBySeller(HttpSession session) {
        Long sellerId = currentUserId(session);
        if (sellerId == null) {
            return Result.failure("未授权");
        }
        return Result.success(orderService.listBySeller(sellerId));
    }

    private Long currentUserId(HttpSession session) {
        Object u = session.getAttribute("currentUser");
        if (u instanceof com.xianyu.vo.UserVO vo) {
            return vo.getId();
        }
        return null;
    }
}
