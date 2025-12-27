package com.xianyu.controller;

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
    public String index(Model model) {
        model.addAttribute("items", itemService.listAll());
        return "index";
    }

    // 商品详情页
    @GetMapping("/item-detail.html")
    public String itemDetail(@RequestParam("id") Long id, Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        itemService.findById(id).ifPresent(item -> model.addAttribute("item", item));
        // 添加当前登录用户信息到模板
        model.addAttribute("currentUser", userDetails != null ? userDetails.getUserVO() : null);
        return "item-detail";
    }

    // 商品发布/编辑页
    @GetMapping("/item-edit.html")
    public String itemEdit(@RequestParam(value = "id", required = false) Long id, Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login.html";
        }
        if (id != null) {
            itemService.findById(id).ifPresent(item -> model.addAttribute("item", item));
        }
        return "item-edit";
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
    public String userCenter(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            model.addAttribute("user", userDetails.getUserVO());
        } else {
            model.addAttribute("user", null);
        }
        return "user-center";
    }
}