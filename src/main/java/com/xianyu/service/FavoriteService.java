package com.xianyu.service;

import com.xianyu.vo.FavoriteVO;

import java.util.List;
import java.util.Optional;

public interface FavoriteService {
    FavoriteVO addFavorite(Long userId, Long itemId);

    boolean removeFavorite(Long userId, Long itemId);

    List<FavoriteVO> listByUser(Long userId);

    Optional<FavoriteVO> findByUserAndItem(Long userId, Long itemId);
}

