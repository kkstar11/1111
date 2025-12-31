package com.xianyu.service;

import com.xianyu.dto.ItemDTO;
import com.xianyu.vo.ItemVO;

import java.util.List;
import java.util.Optional;

public interface
ItemService {
    ItemVO create(ItemDTO dto, Long ownerId);

    Optional<ItemVO> update(Long id, ItemDTO dto, Long ownerId);

    boolean delete(Long id, Long ownerId);

    Optional<ItemVO> findById(Long id);

    List<ItemVO> listAll();

    List<ItemVO> listOnSale();

    List<ItemVO> listByOwnerId(Long ownerId);

    boolean updateStatus(Long id, Integer status, Long ownerId);
}

