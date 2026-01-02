package com.xianyu.controller;

import com.xianyu.dto.ItemDTO;
import com.xianyu.dto.StatusUpdateDTO;
import com.xianyu.service.ItemService;
import com.xianyu.util.Result;
import com.xianyu.vo.ItemVO;
import com.xianyu.security.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Result<ItemVO> create(@RequestBody ItemDTO dto, @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long ownerId = userDetails.getUserVO().getId();
        try {
            ItemVO item = itemService.create(dto, ownerId);
            System.out.println("[ITEM-CREATE] ownerId: " + ownerId + " 创建商品成功: " + item);
            return Result.success(item);
        } catch (IllegalArgumentException e) {
            System.out.println("[ITEM-CREATE] 创建商品失败: " + e.getMessage());
            return Result.failure(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<ItemVO> update(@PathVariable Long id, @RequestBody ItemDTO dto, @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long ownerId = userDetails.getUserVO().getId();
        Optional<ItemVO> updated = itemService.update(id, dto, ownerId);
        return updated.map(Result::success).orElseGet(() -> Result.failure("未找到或无权限"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id, @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long ownerId = userDetails.getUserVO().getId();
        boolean ok = itemService.delete(id, ownerId);
        return ok ? Result.success(null) : Result.failure("未找到或无权限");
    }

    @GetMapping("/{id}")
    public Result<ItemVO> get(@PathVariable Long id) {
        return itemService.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("商品未找到"));
    }

    @GetMapping
    public Result<List<ItemVO>> list() {
        return Result.success(itemService.listAll());
    }

    @GetMapping("/my")
    public Result<List<ItemVO>> myItems(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long ownerId = userDetails.getUserVO().getId();
        List<ItemVO> list = itemService.listByOwnerId(ownerId);
        return Result.success(list);
    }

    @PutMapping("/{id}/status")
    public Result<ItemVO> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO dto, @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long ownerId = userDetails.getUserVO().getId();
        Integer status = dto.getStatus();
        if (status == null) {
            return Result.failure("状态不能为空");
        }
        boolean ok = itemService.updateStatus(id, status, ownerId);
        if (!ok) {
            return Result.failure("更新失败或无权限");
        }
        // 返回更新后的商品信息
        return itemService.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("商品未找到"));
    }
}