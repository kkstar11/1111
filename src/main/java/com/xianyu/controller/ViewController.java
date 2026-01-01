package com.xianyu.controller;

import com.xianyu.dto.ItemDTO;
import com.xianyu.security.MyUserDetails;
import com.xianyu.service.FavoriteService;
import com.xianyu.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class ViewController {

    private final ItemService itemService;
    private final FavoriteService favoriteService;

    @Autowired
    public ViewController(ItemService itemService, FavoriteService favoriteService) {
        this.itemService = itemService;
        this.favoriteService = favoriteService;
    }

    // 首页/商品列表
    @GetMapping({"/", "/index.html", "/index"})
    public String index(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        // If admin is logged in, redirect to admin panel
        if (isAdmin(userDetails)) {
            return "redirect:/admin.html";
        }
        model.addAttribute("items", itemService.listOnSale());
        return "index";
    }

    // 商品详情页
    @GetMapping("/item-detail.html")
    public String itemDetail(@RequestParam("id") Long id, Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        // If admin is logged in, redirect to admin panel
        if (isAdmin(userDetails)) {
            return "redirect:/admin.html";
        }
        itemService.findById(id).ifPresent(item -> {
            model.addAttribute("item", item);
            // 判断当前用户是否为商品发布者
            boolean isOwner = userDetails != null && userDetails.getUserVO() != null 
                    && item.getOwnerId() != null 
                    && item.getOwnerId().equals(userDetails.getUserVO().getId());
            model.addAttribute("isOwner", isOwner);
        });
        return "item-detail";
    }

    // 商品发布/编辑页
    @GetMapping("/item-edit.html")
    public String itemEdit(@RequestParam(value = "id", required = false) Long id, Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login.html";
        }
        // If admin is logged in, redirect to admin panel
        if (isAdmin(userDetails)) {
            return "redirect:/admin.html";
        }
        if (id != null) {
            itemService.findById(id).ifPresent(item -> model.addAttribute("item", item));
        }
        return "item-edit";
    }

    @PostMapping("/item-edit.html")
    public String saveItemEdit(@ModelAttribute ItemDTO itemDTO, 
                               @AuthenticationPrincipal MyUserDetails userDetails,
                               Model model) {
        if (userDetails == null) {
            return "redirect:/login.html";
        }
        Long ownerId = userDetails.getUserVO().getId();
        // 判断是新建还是编辑
        if (itemDTO.getId() == null) {
            itemService.create(itemDTO, ownerId);
        } else {
            itemService.update(itemDTO.getId(), itemDTO, ownerId);
        }
        return "redirect:/my-orders.html";
    }

    // 登录页
    @GetMapping("/login.html")
    public String login() {
        return "login";
    }

    // 注册页
    @GetMapping("/register.html")
    public String register() {
        return "register";
    }

    @GetMapping("/my-orders.html")
    public String myOrders(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        // If admin is logged in, redirect to admin panel
        if (isAdmin(userDetails)) {
            return "redirect:/admin.html";
        }
        Long userId = null;
        if (userDetails != null) userId = userDetails.getUserVO().getId();

        System.out.println("当前登录userId = " + userId);

        var favorites = userId == null ? Collections.emptyList() : favoriteService.listByUser(userId);
        var myItems = userId == null ? Collections.emptyList() : itemService.listByOwnerId(userId);

        System.out.println("查到我的商品条数: " + myItems.size());

        model.addAttribute("favorites", favorites);
        model.addAttribute("myItems", myItems);
        return "my-orders";
    }

    @GetMapping("/user-center.html")
    public String userCenter(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        // If admin is logged in, redirect to admin panel
        if (isAdmin(userDetails)) {
            return "redirect:/admin.html";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            MyUserDetails details = (MyUserDetails) auth.getPrincipal();
            model.addAttribute("user", details.getUserVO());
        } else {
            model.addAttribute("user", null);
        }
        return "user-center";
    }

    @GetMapping("/admin.html")
    public String admin(@AuthenticationPrincipal MyUserDetails userDetails) {
        // 检查是否为管理员 (Spring Security already handles this, but double-check for safety)
        if (!isAdmin(userDetails)) {
            return "redirect:/login.html";  // 非管理员重定向到登录页
        }
        return "admin";
    }

    // Helper method to check if user is admin
    private boolean isAdmin(MyUserDetails userDetails) {
        return userDetails != null && userDetails.getUserVO() != null 
                && userDetails.getUserVO().getRole() != null 
                && userDetails.getUserVO().getRole() == 1;
    }
}