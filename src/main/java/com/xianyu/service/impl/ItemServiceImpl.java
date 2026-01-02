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
        item.setTitle(dto.getName());
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
        existing.setTitle(dto.getName());
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
        // 不允许用户通过编辑接口修改status，status只能通过专门的审核接口或updateStatus接口修改
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

    @Override
    public List<ItemVO> listOnSale() {
        // 只返回status=1(上架)的商品，供首页展示
        return itemMapper.findByStatus(STATUS_ON_SALE).stream().map(this::toVO).toList();
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
        ItemVO vo = new ItemVO();
        vo.setId(item.getId());
        vo.setName(item.getTitle());
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
        // Mapper查出商品实体
        List<Item> entities = itemMapper.findBySeller(ownerId);
        // 转为VO列表
        return entities.stream().map(this::toVO).toList();
    }

    @Override
    public boolean updateStatus(Long id, Integer status, Long ownerId) {
        // 验证status值是否合法（0-4之间）
        if (status == null || status < 0 || status > STATUS_REJECTED) {
            return false;
        }
        // 查询商品是否存在，并验证是否为所有者
        Item existing = itemMapper.findById(id).orElse(null);
        if (existing == null || (ownerId != null && !ownerId.equals(existing.getSellerId()))) {
            return false;
        }
        
        if (existing.getStatus() != null) {
            int currentStatus = existing.getStatus();
            
            // 已售出的商品不允许修改状态
            if (currentStatus == STATUS_SOLD) {
                return false;
            }
            
            // 待审核和被驳回的商品不允许用户自行修改状态
            // 待审核商品需要等待管理员审核
            // 被驳回商品需要删除后重新发布
            if (currentStatus == STATUS_PENDING || currentStatus == STATUS_REJECTED) {
                return false;
            }
            
            // 普通用户只能在上架(1)和下架(3)之间切换
            // 允许的转换: 1→3, 3→1, 或保持不变(1→1, 3→3)
            boolean isValidTransition = 
                (currentStatus == STATUS_ON_SALE && (status == STATUS_ON_SALE || status == STATUS_OFF_SALE)) ||
                (currentStatus == STATUS_OFF_SALE && (status == STATUS_ON_SALE || status == STATUS_OFF_SALE));
            
            if (!isValidTransition) {
                return false;
            }
        }
        
        // 更新状态
        int updated = itemMapper.updateStatus(id, status);
        return updated > 0;
    }
}

