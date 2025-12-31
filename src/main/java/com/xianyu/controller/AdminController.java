package com.xianyu.controller;

import com.xianyu.dao.ItemMapper;
import com.xianyu.dao.UserMapper;
import com.xianyu.entity.Item;
import com.xianyu.entity.User;
import com.xianyu.security.MyUserDetails;
import com.xianyu.util.Result;
import com.xianyu.vo.ItemVO;
import com.xianyu.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public AdminController(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    // 检查是否为管理员
    private boolean isAdmin(MyUserDetails userDetails) {
        return userDetails != null && userDetails.getUserVO() != null 
                && userDetails.getUserVO().getRole() != null 
                && userDetails.getUserVO().getRole() == 1;
    }

    // 获取所有用户列表
    @GetMapping("/users")
    public Result<List<UserVO>> getAllUsers(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("unauthorized: admin only");
        }
        List<User> users = userMapper.findAll();
        List<UserVO> userVOs = users.stream().map(this::toUserVO).toList();
        return Result.success(userVOs);
    }

    // 更新用户状态（启用/禁用）
    @PutMapping("/users/{id}/status")
    public Result<String> updateUserStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> payload,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("unauthorized: admin only");
        }
        Integer status = payload.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.failure("invalid status: must be 0 or 1");
        }
        int updated = userMapper.updateStatus(id, status);
        if (updated > 0) {
            return Result.success("user status updated successfully");
        }
        return Result.failure("update failed: user not found");
    }

    // 获取待审核商品列表
    @GetMapping("/items/pending")
    public Result<List<ItemVO>> getPendingItems(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("unauthorized: admin only");
        }
        List<Item> items = itemMapper.findByStatus(0); // 0 = pending
        List<ItemVO> itemVOs = items.stream().map(this::toItemVO).toList();
        return Result.success(itemVOs);
    }

    // 审核通过商品
    @PutMapping("/items/{id}/approve")
    public Result<String> approveItem(
            @PathVariable Long id,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("unauthorized: admin only");
        }
        int updated = itemMapper.updateStatus(id, 1); // 1 = approved/on sale
        if (updated > 0) {
            return Result.success("item approved successfully");
        }
        return Result.failure("approval failed: item not found");
    }

    // 驳回商品
    @PutMapping("/items/{id}/reject")
    public Result<String> rejectItem(
            @PathVariable Long id,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("unauthorized: admin only");
        }
        int updated = itemMapper.updateStatus(id, 4); // 4 = rejected
        if (updated > 0) {
            return Result.success("item rejected successfully");
        }
        return Result.failure("rejection failed: item not found");
    }

    // 将User实体转换为UserVO
    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setStudentId(user.getStudentId());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setCollege(user.getCollege());
        vo.setMajor(user.getMajor());
        vo.setGrade(user.getGrade());
        vo.setStatus(user.getStatus());
        vo.setRole(user.getRole());
        // 不设置password，避免密码哈希泄露
        return vo;
    }

    // 将Item实体转换为ItemVO
    private ItemVO toItemVO(Item item) {
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
}
