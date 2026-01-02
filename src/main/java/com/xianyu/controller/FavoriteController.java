package com.xianyu.controller;

import com.xianyu.service.FavoriteService;
import com.xianyu.util.Result;
import com.xianyu.vo.FavoriteVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public Result<FavoriteVO> add(Long itemId, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return Result.failure("未授权");
        }
        return Result.success(favoriteService.addFavorite(userId, itemId));
    }

    @DeleteMapping
    public Result<Void> remove(Long itemId, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return Result.failure("未授权");
        }
        boolean removed = favoriteService.removeFavorite(userId, itemId);
        return removed ? Result.success(null) : Result.failure("收藏未找到");
    }

    @GetMapping
    public Result<List<FavoriteVO>> list(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return Result.failure("未授权");
        }
        return Result.success(favoriteService.listByUser(userId));
    }

    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long itemId, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return Result.success(false);
        }
        boolean isFavorited = favoriteService.findByUserAndItem(userId, itemId).isPresent();
        return Result.success(isFavorited);
    }

    private Long currentUserId(HttpSession session) {
        Object u = session.getAttribute("currentUser");
        if (u instanceof com.xianyu.vo.UserVO vo) {
            return vo.getId();
        }
        return null;
    }
}

