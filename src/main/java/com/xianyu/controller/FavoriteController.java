package com.xianyu.controller;

import com.xianyu.service.FavoriteService;
import com.xianyu.util.Result;
import com.xianyu.vo.FavoriteVO;
import com.xianyu.security.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 收藏
    @PostMapping
    public Result<FavoriteVO> add(@RequestParam("itemId") Long itemId,
                                  @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long userId = userDetails.getUserVO().getId();
        return Result.success(favoriteService.addFavorite(userId, itemId));
    }

    // 取消收藏
    @DeleteMapping
    public Result<Void> remove(@RequestParam("itemId") Long itemId,
                               @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long userId = userDetails.getUserVO().getId();
        boolean removed = favoriteService.removeFavorite(userId, itemId);
        return removed ? Result.success(null) : Result.failure("收藏未找到");
    }

    // 获取收藏列表
    @GetMapping
    public Result<List<FavoriteVO>> list(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.failure("未授权");
        }
        Long userId = userDetails.getUserVO().getId();
        return Result.success(favoriteService.listByUser(userId));
    }

    // 检查是否收藏某商品
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam("itemId") Long itemId,
                                 @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return Result.success(false);
        }
        Long userId = userDetails.getUserVO().getId();
        boolean isFavorited = favoriteService.findByUserAndItem(userId, itemId).isPresent();
        return Result.success(isFavorited);
    }
}