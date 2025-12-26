package com.xianyu.controller;

import com.xianyu.security.MyUserDetails;
import com.xianyu.service.FavoriteService;
import com.xianyu.service.ItemService;
import com.xianyu.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
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
    public String itemDetail(@RequestParam("id") Long id, Model model) {
        itemService.findById(id).ifPresent(item -> model.addAttribute("item", item));
        return "item-detail";
    }

    // 商品发布/编辑页
    @GetMapping("/item-edit.html")
    public String itemEdit(@RequestParam(value = "id", required = false) Long id, Model model) {
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

    // 我的订单/收藏/发布
    @GetMapping("/my-orders.html")
    public String myOrders(Model model, HttpSession session) {
        Long userId = null;
        Object u = session.getAttribute("currentUser");
        if (u instanceof UserVO vo) userId = vo.getId();
        model.addAttribute("favorites", userId == null ? Collections.emptyList() : favoriteService.listByUser(userId));
        // “已发布”商品（核心改动这行！）
        model.addAttribute(
                "myItems",
                userId == null
                        ? Collections.emptyList()
                        : itemService.listByOwnerId(userId)
        );
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