package com.shiyiju.modules.cart.service;

import com.shiyiju.modules.cart.entity.CartEntity;
import com.shiyiju.modules.cart.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    /**
     * 获取用户购物车列表
     */
    public List<Map<String, Object>> getCartList(Long userId) {
        List<CartEntity> items = cartMapper.findByUserId(userId);
        return items.stream().map(this::toCartItem).toList();
    }

    /**
     * 添加商品到购物车
     */
    public Map<String, Object> addToCart(Long userId, Long artworkId, Integer quantity) {
        // 检查是否已存在
        CartEntity existing = cartMapper.findByUserAndArtwork(userId, artworkId);
        
        if (existing != null) {
            // 已存在，增加数量
            int newQuantity = existing.getQuantity() + quantity;
            cartMapper.updateQuantity(existing.getId(), newQuantity);
            existing.setQuantity(newQuantity);
            return toCartItem(existing);
        } else {
            // 新增
            CartEntity cart = new CartEntity();
            cart.setUserId(userId);
            cart.setArtworkId(artworkId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
            
            // 查询完整的商品信息
            List<CartEntity> items = cartMapper.findByUserId(userId);
            CartEntity inserted = items.stream()
                .filter(i -> i.getArtworkId().equals(artworkId))
                .findFirst()
                .orElse(cart);
            return toCartItem(inserted);
        }
    }

    /**
     * 更新购物车商品数量
     */
    public boolean updateQuantity(Long userId, Long cartId, Integer quantity) {
        return cartMapper.updateQuantity(cartId, quantity) > 0;
    }

    /**
     * 从购物车移除商品
     */
    public boolean removeFromCart(Long userId, Long cartId) {
        return cartMapper.deleteById(cartId) > 0;
    }

    /**
     * 清空购物车
     */
    public void clearCart(Long userId) {
        List<CartEntity> items = cartMapper.findByUserId(userId);
        for (CartEntity item : items) {
            cartMapper.deleteById(item.getId());
        }
    }

    /**
     * 获取购物车商品数量
     */
    public int getCartCount(Long userId) {
        return cartMapper.countByUserId(userId);
    }

    private Map<String, Object> toCartItem(CartEntity entity) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", entity.getId());
        item.put("artworkId", entity.getArtworkId());
        item.put("title", entity.getArtworkTitle());
        item.put("coverUrl", entity.getArtworkCoverUrl());
        item.put("artistName", entity.getArtistName());
        item.put("price", entity.getPrice());
        item.put("stock", entity.getStock() != null ? entity.getStock() : 0);
        item.put("category", entity.getCategory());
        item.put("quantity", entity.getQuantity());
        item.put("selected", true); // 默认选中
        return item;
    }
}
