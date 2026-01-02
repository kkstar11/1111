package com.xianyu.service.impl;

import com.xianyu.dao.ItemMapper;
import com.xianyu.dto.ItemDTO;
import com.xianyu.entity.Item;
import com.xianyu.service.ItemService;
import com.xianyu.vo.ItemVO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    // 商品状态常量
    private static final int STATUS_PENDING = 0;    // 待审核
    private static final int STATUS_ON_SALE = 1;    // 上架
    private static final int STATUS_SOLD = 2;       // 已售出
    private static final int STATUS_OFF_SALE = 3;   // 下架
    private static final int STATUS_REJECTED = 4;   // 审核驳回

    public ItemServiceImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemVO create(ItemDTO dto, Long ownerId) {
        validate(dto);
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setOriginalPrice(dto.getOriginalPrice() != null ? dto.getOriginalPrice() : dto.getPrice());
        item.setCategory(dto.getCategory() != null ? dto.getCategory() : "default");
        item.setConditions(dto.getConditions() != null ? dto.getConditions() : 2);
        item.setStatus(STATUS_PENDING);  // 新创建的商品设置为待审核状态
        item.setSellerId(ownerId);
        item.setContactWay(dto.getContactWay());
        item.setLocation(dto.getLocation());
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
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        if (dto.getOriginalPrice() != null) {
            existing.setOriginalPrice(dto.getOriginalPrice());
        }
        if (dto.getCategory() != null) {
            existing.setCategory(dto.getCategory());
        }
        if (dto.getConditions() != null) {
            existing.setConditions(dto.getConditions());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        if (dto.getContactWay() != null) {
            existing.setContactWay(dto.getContactWay());
        }
        if (dto.getLocation() != null) {
            existing.setLocation(dto.getLocation());
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
            throw new IllegalArgumentException("商品数据不能为空");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("价格必须大于等于 0");
        }
    }

    private ItemVO toVO(Item item) {
        return getItemVO(item);
    }

    @NonNull
    public static ItemVO getItemVO(Item item) {
        ItemVO vo = new ItemVO();
        vo.setId(item.getId());
        vo.setName(item.getName());
        vo.setDescription(item.getDescription());
        vo.setPrice(item.getPrice());
        vo.setOriginalPrice(item.getOriginalPrice());
        vo.setCategory(item.getCategory());
        vo.setConditions(item.getConditions());
        vo.setStatus(item.getStatus());
        vo.setOwnerId(item.getSellerId());
        vo.setContactWay(item.getContactWay());
        vo.setLocation(item.getLocation());
        vo.setImageUrls(item.getImageUrls());
        return vo;
    }

    @Override
    public List<ItemVO> listByOwnerId(Long ownerId) {
        List<Item> entities = itemMapper.findBySeller(ownerId);

        return entities.stream().map(this::toVO).toList();
    }

    @Override
    public boolean updateStatus(Long id, Integer status, Long ownerId) {
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

    @Override
    public List<ItemVO> listOnSale() {
        List<Item> items = itemMapper.findByStatus(STATUS_ON_SALE);
        return items.stream().map(this::toVO).toList();
    }
}

