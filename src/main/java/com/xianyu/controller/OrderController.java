package com.xianyu.controller;

import com.xianyu.dto.OrderCreateDTO;
import com.xianyu.service.OrderService;
import com.xianyu.util.Result;
import com.xianyu.vo.OrderVO;
import com.xianyu.security.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 下单
    @PostMapping
    public Result<OrderVO> create(@RequestBody OrderCreateDTO dto,
                                  @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long buyerId = userDetails.getUserVO().getId();
        try {
            return Result.success(orderService.createOrder(dto, buyerId));
        } catch (IllegalArgumentException e) {
            return Result.failure(e.getMessage());
        }
    }

    // 完成订单
    @PutMapping("/{id}/finish")
    public Result<OrderVO> finish(@PathVariable("id") Long id,
                                  @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long userId = userDetails.getUserVO().getId();
        Optional<OrderVO> result = orderService.finishOrder(id, userId);
        return result.map(Result::success).orElseGet(() -> Result.failure("未找到或无权限"));
    }

    // 取消订单
    @PutMapping("/{id}/cancel")
    public Result<OrderVO> cancel(@PathVariable Long id,
                                  @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long userId = userDetails.getUserVO().getId();
        Optional<OrderVO> result = orderService.cancelOrder(id, userId);
        return result.map(Result::success).orElseGet(() -> Result.failure("未找到或无权限"));
    }

    // 订单详情
    @GetMapping("/{id}")
    public Result<OrderVO> get(@PathVariable Long id,
                               @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long userId = userDetails.getUserVO().getId();
        return orderService.findById(id)
                .map(order -> {
                    if (order.getBuyerId().equals(userId) || order.getSellerId().equals(userId)) {
                        return Result.success(order);
                    }
                    return Result.<OrderVO>failure("无权限");
                })
                .orElseGet(() -> Result.failure("订单未找到"));
    }

    // 买家订单列表
    @GetMapping("/buyer")
    public Result<List<OrderVO>> listByBuyer(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long buyerId = userDetails.getUserVO().getId();
        return Result.success(orderService.listByBuyer(buyerId));
    }

    // 卖家订单列表
    @GetMapping("/seller")
    public Result<List<OrderVO>> listBySeller(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long sellerId = userDetails.getUserVO().getId();
        return Result.success(orderService.listBySeller(sellerId));
    }
}