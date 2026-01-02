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

import static com.xianyu.service.impl.ItemServiceImpl.getItemVO;

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
            return Result.failure("未授权：仅限管理员");
        }
        List<User> users = userMapper.findAll();
        List<UserVO> userVOs = users.stream().map(this::toUserVO).toList();
        return Result.success(userVOs);
    }

    // 更新用户状态（启用/禁用）
    @PutMapping("/users/{id}/status")
    public Result<String> updateUserStatus(
            @PathVariable ("id") Long id,
            @RequestBody Map<String, Integer> payload,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("未授权：仅限管理员");
        }
        Integer status = payload.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.failure("状态无效：必须为 0 或 1");
        }
        int updated = userMapper.updateStatus(id, status);
        if (updated > 0) {
            return Result.success("用户状态更新成功");
        }
        return Result.failure("更新失败：用户未找到");
    }

    // 获取待审核商品列表
    @GetMapping("/items/pending")
    public Result<List<ItemVO>> getPendingItems(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("未授权：仅限管理员");
        }
        List<Item> items = itemMapper.findByStatus(0); // 0 = pending
        List<ItemVO> itemVOs = items.stream().map(this::toItemVO).toList();
        return Result.success(itemVOs);
    }

    // 审核通过商品
    @PutMapping("/items/{id}/approve")
    public Result<String> approveItem(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("未授权：仅限管理员");
        }
        int updated = itemMapper.updateStatus(id, 1); // 1 = approved/on sale
        if (updated > 0) {
            return Result.success("商品审核通过");
        }
        return Result.failure("审核失败：商品未找到");
    }

    // 驳回商品
    @PutMapping("/items/{id}/reject")
    public Result<String> rejectItem(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return Result.failure("未授权：仅限管理员");
        }
        int updated = itemMapper.updateStatus(id, 4); // 4 = rejected
        if (updated > 0) {
            return Result.success("商品已驳回");
        }
        return Result.failure("驳回失败：商品未找到");
    }

    // 将User实体转换为UserVO
    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setStudentId(user.getStudentId());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setRole(user.getRole());
        // 不设置password，避免密码哈希泄露
        return vo;
    }

    // 将Item实体转换为ItemVO
    private ItemVO toItemVO(Item item) {
        return getItemVO(item);
    }
}
