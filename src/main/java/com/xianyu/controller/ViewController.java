package com.xianyu.controller;

import com.xianyu.service.FavoriteService;
import com.xianyu.service.ItemService;
import com.xianyu.vo.FavoriteVO;
import com.xianyu.vo.ItemVO;
import com.xianyu.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    private final ItemService itemService;
    private final FavoriteService favoriteService;

    public ViewController(ItemService itemService, FavoriteService favoriteService) {
        this.itemService = itemService;
        this.favoriteService = favoriteService;
    }

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        List<ItemVO> items = itemService.listAll();
        model.addAttribute("items", items);
        return "index";
    }

    @GetMapping("/item-detail.html")
    public String itemDetail(@RequestParam Long id, Model model) {
        Optional<ItemVO> item = itemService.findById(id);
        item.ifPresent(i -> model.addAttribute("item", i));
        return "item-detail";
    }

    @GetMapping("/item-edit.html")
    public String itemEdit(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            Optional<ItemVO> item = itemService.findById(id);
            item.ifPresent(i -> model.addAttribute("item", i));
        }
        return "item-edit";
    }

    @GetMapping("/my-orders.html")
    public String myOrders(Model model, HttpSession session) {
        Long currentUserId = currentUserId(session);
        if (currentUserId != null) {
            // Get user's favorites
            List<FavoriteVO> favorites = favoriteService.listByUser(currentUserId);
            model.addAttribute("favorites", favorites);

            // Get items owned by current user
            List<ItemVO> allItems = itemService.listAll();
            List<ItemVO> myItems = allItems.stream()
                    .filter(item -> currentUserId.equals(item.getOwnerId()))
                    .collect(Collectors.toList());
            model.addAttribute("myItems", myItems);
        }
        return "my-orders";
    }

    @GetMapping("/user-center.html")
    public String userCenter(Model model, HttpSession session) {
        UserVO currentUser = getCurrentUser(session);
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
        }
        return "user-center";
    }

    @GetMapping("/login.html")
    public String login() {
        return "login";
    }

    @GetMapping("/register.html")
    public String register() {
        return "register";
    }

    private Long currentUserId(HttpSession session) {
        Object u = session.getAttribute("currentUser");
        if (u instanceof UserVO vo) {
            return vo.getId();
        }
        return null;
    }

    private UserVO getCurrentUser(HttpSession session) {
        Object u = session.getAttribute("currentUser");
        if (u instanceof UserVO vo) {
            return vo;
        }
        return null;
    }
}
