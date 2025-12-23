package com.xianyu.service.impl;

import com.xianyu.dao.FavoriteMapper;
import com.xianyu.entity.Favorite;
import com.xianyu.service.FavoriteService;
import com.xianyu.vo.FavoriteVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper) {
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public FavoriteVO addFavorite(Long userId, Long itemId) {
        Favorite existing = favoriteMapper.find(userId, itemId).orElse(null);
        if (existing != null) return toVO(existing);
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setItemId(itemId);
        favoriteMapper.insert(favorite);
        return toVO(favorite);
    }

    @Override
    public boolean removeFavorite(Long userId, Long itemId) {
        return favoriteMapper.delete(userId, itemId) > 0;
    }

    @Override
    public List<FavoriteVO> listByUser(Long userId) {
        return favoriteMapper.listByUser(userId).stream().map(this::toVO).toList();
    }

    @Override
    public Optional<FavoriteVO> findByUserAndItem(Long userId, Long itemId) {
        return favoriteMapper.find(userId, itemId).map(this::toVO);
    }

    private FavoriteVO toVO(Favorite favorite) {
        FavoriteVO vo = new FavoriteVO();
        vo.setId(favorite.getId());
        vo.setUserId(favorite.getUserId());
        vo.setItemId(favorite.getItemId());
        return vo;
    }
}

