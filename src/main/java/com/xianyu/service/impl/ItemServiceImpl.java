package com.xianyu.service.impl;

import com.xianyu.dao.ItemMapper;
import com.xianyu.dto.ItemDTO;
import com.xianyu.entity.Item;
import com.xianyu.service.ItemService;
import com.xianyu.vo.ItemVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    // 商品状态常量
    private static final int STATUS_ON_SALE = 1;    // 上架
    private static final int STATUS_OFF_SALE = 2;   // 下架
    private static final int STATUS_SOLD = 3;       // 已售出

    public ItemServiceImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemVO create(ItemDTO dto, Long ownerId) {
        validate(dto);
        Item item = new Item();
        item.setTitle(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setOriginalPrice(dto.getOriginalPrice() != null ? dto.getOriginalPrice() : dto.getPrice());
        item.setCategory(dto.getCategory() != null ? dto.getCategory() : "default");
        item.setItemCondition(dto.getItemCondition() != null ? dto.getItemCondition() : 2);
        item.setStatus(STATUS_ON_SALE);
        item.setSellerId(ownerId);
        item.setContactWay(dto.getContactWay());
        item.setItemLocation(dto.getItemLocation());
        item.setImageUrls(dto.getImageUrls());
        item.setViewCount(0);
        item.setLikeCount(0);
        itemMapper.insert(item);
        return itemMapper.findById(item.getId()).map(this::toVO).orElseGet(() -> toVO(item));
    }

    @Override
    public Optional<ItemVO> update(Long id, ItemDTO dto, Long ownerId) {
        Item existing = itemMapper.findById(id).orElse(null);
        if (existing == null || (ownerId != null && !ownerId.equals(existing.getSellerId()))) {
            return Optional.empty();
        }
        validate(dto);
        existing.setTitle(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        if (dto.getOriginalPrice() != null) {
            existing.setOriginalPrice(dto.getOriginalPrice());
        }
        if (dto.getCategory() != null) {
            existing.setCategory(dto.getCategory());
        }
        if (dto.getItemCondition() != null) {
            existing.setItemCondition(dto.getItemCondition());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        if (dto.getContactWay() != null) {
            existing.setContactWay(dto.getContactWay());
        }
        if (dto.getItemLocation() != null) {
            existing.setItemLocation(dto.getItemLocation());
        }
        if (dto.getImageUrls() != null) {
            existing.setImageUrls(dto.getImageUrls());
        }
        itemMapper.update(existing);
        return itemMapper.findById(id).map(this::toVO);
    }

    @Override
    public boolean delete(Long id, Long ownerId) {
        Item existing = itemMapper.findById(id).orElse(null);
        if (existing == null || (ownerId != null && !ownerId.equals(existing.getSellerId()))) {
            return false;
        }
        itemMapper.delete(id);
        return true;
    }

    @Override
    public Optional<ItemVO> findById(Long id) {
        return itemMapper.findById(id).map(this::toVO);
    }

    @Override
    public List<ItemVO> listAll() {
        return itemMapper.findAll().stream().map(this::toVO).toList();
    }

    private void validate(ItemDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("item payload required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
    }

    private ItemVO toVO(Item item) {
        ItemVO vo = new ItemVO();
        vo.setId(item.getId());
        vo.setName(item.getTitle());
        vo.setDescription(item.getDescription());
        vo.setPrice(item.getPrice());
        vo.setOriginalPrice(item.getOriginalPrice());
        vo.setCategory(item.getCategory());
        vo.setItemCondition(item.getItemCondition());
        vo.setStatus(item.getStatus());
        vo.setOwnerId(item.getSellerId());
        vo.setContactWay(item.getContactWay());
        vo.setItemLocation(item.getItemLocation());
        vo.setImageUrls(item.getImageUrls());
        return vo;
    }

    @Override
    public List<ItemVO> listByOwnerId(Long ownerId) {
        // Mapper查出商品实体
        List<Item> entities = itemMapper.findBySeller(ownerId);
        // 转为VO列表
        return entities.stream().map(this::toVO).toList();
    }

    @Override
    public boolean updateStatus(Long id, Integer status, Long ownerId) {
        // 验证status值是否合法
        // 只允许在"上架"(1)和"下架"(2)之间切换
        // 已售出(3)的商品不允许通过此接口修改状态
        if (status == null || (status != STATUS_ON_SALE && status != STATUS_OFF_SALE)) {
            return false;
        }
        // 查询商品是否存在，并验证是否为所有者
        Item existing = itemMapper.findById(id).orElse(null);
        if (existing == null || (ownerId != null && !ownerId.equals(existing.getSellerId()))) {
            return false;
        }
        // 不允许修改已售出商品的状态
        if (existing.getStatus() != null && existing.getStatus() == STATUS_SOLD) {
            return false;
        }
        // 更新状态
        int updated = itemMapper.updateStatus(id, status);
        return updated > 0;
    }
}

