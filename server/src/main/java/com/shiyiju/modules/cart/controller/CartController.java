package com.shiyiju.modules.cart.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.cart.service.CartService;
import com.shiyiju.security.CurrentUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> getCartList() {
        Long userId = CurrentUserHolder.get();
        List<Map<String, Object>> items = cartService.getCartList(userId);
        return ApiResponse.success(items);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public ApiResponse<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request) {
        Long userId = CurrentUserHolder.get();
        
        Long artworkId = Long.valueOf(request.get("artworkId").toString());
        Integer quantity = request.containsKey("quantity") 
            ? Integer.valueOf(request.get("quantity").toString()) 
            : 1;
        
        Map<String, Object> item = cartService.addToCart(userId, artworkId, quantity);
        return ApiResponse.success("已加入购物车", item);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public ApiResponse<Void> updateQuantity(@RequestBody Map<String, Object> request) {
        Long userId = CurrentUserHolder.get();
        
        Long cartId = Long.valueOf(request.get("cartId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        cartService.updateQuantity(userId, cartId, quantity);
        return ApiResponse.success("更新成功", null);
    }

    /**
     * 从购物车移除商品
     */
    @DeleteMapping("/remove/{cartId}")
    public ApiResponse<Void> removeFromCart(@PathVariable Long cartId) {
        Long userId = CurrentUserHolder.get();
        
        cartService.removeFromCart(userId, cartId);
        return ApiResponse.success("已移除", null);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart() {
        Long userId = CurrentUserHolder.get();
        
        cartService.clearCart(userId);
        return ApiResponse.success("已清空", null);
    }

    /**
     * 获取购物车商品数量
     */
    @GetMapping("/count")
    public ApiResponse<Map<String, Object>> getCartCount() {
        Long userId = CurrentUserHolder.get();
        
        int count = cartService.getCartCount(userId);
        return ApiResponse.success(Map.of("count", count));
    }
}
