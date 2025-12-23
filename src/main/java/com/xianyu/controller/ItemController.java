package com.xianyu.controller;

import com.xianyu.dto.ItemDTO;
import com.xianyu.service.ItemService;
import com.xianyu.util.Result;
import com.xianyu.vo.ItemVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Result<ItemVO> create(@RequestBody ItemDTO dto, HttpSession session) {
        Long ownerId = currentUserId(session);
        if (ownerId == null) {
            return Result.failure("unauthorized");
        }
        try {
            return Result.success(itemService.create(dto, ownerId));
        } catch (IllegalArgumentException e) {
            return Result.failure(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<ItemVO> update(@PathVariable Long id, @RequestBody ItemDTO dto, HttpSession session) {
        Long ownerId = currentUserId(session);
        if (ownerId == null) {
            return Result.failure("unauthorized");
        }
        Optional<ItemVO> updated = itemService.update(id, dto, ownerId);
        return updated.map(Result::success).orElseGet(() -> Result.failure("not found or no permission"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session) {
        Long ownerId = currentUserId(session);
        if (ownerId == null) {
            return Result.failure("unauthorized");
        }
        boolean ok = itemService.delete(id, ownerId);
        return ok ? Result.success(null) : Result.failure("not found or no permission");
    }

    @GetMapping("/{id}")
    public Result<ItemVO> get(@PathVariable Long id) {
        return itemService.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("item not found"));
    }

    @GetMapping
    public Result<List<ItemVO>> list() {
        return Result.success(itemService.listAll());
    }

    private Long currentUserId(HttpSession session) {
        Object u = session.getAttribute("currentUser");
        if (u instanceof com.xianyu.vo.UserVO vo) {
            return vo.getId();
        }
        return null;
    }
}

