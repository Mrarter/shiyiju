package com.shiyiju.modules.cart.mapper;

import com.shiyiju.modules.cart.entity.CartEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("SELECT c.*, a.title AS artwork_title, m.media_url AS artwork_cover_url, " +
            "ap.artist_name AS artist_name, a.current_price AS price, a.inventory_available AS stock, a.category " +
            "FROM user_cart c " +
            "LEFT JOIN artwork a ON c.artwork_id = a.id " +
            "LEFT JOIN artwork_media m ON a.id = m.artwork_id AND m.is_main = 1 " +
            "LEFT JOIN artist_profile ap ON a.artist_id = ap.id " +
            "WHERE c.user_id = #{userId} AND c.deleted = 0 " +
            "ORDER BY c.created_at DESC")
    @Results({
        @Result(property = "artworkTitle", column = "artwork_title"),
        @Result(property = "artworkCoverUrl", column = "artwork_cover_url"),
        @Result(property = "artistName", column = "artist_name")
    })
    List<CartEntity> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_cart WHERE user_id = #{userId} AND artwork_id = #{artworkId} AND deleted = 0 LIMIT 1")
    CartEntity findByUserAndArtwork(@Param("userId") Long userId, @Param("artworkId") Long artworkId);

    @Insert("INSERT INTO user_cart (user_id, artwork_id, quantity, created_at, updated_at, deleted) " +
            "VALUES (#{userId}, #{artworkId}, #{quantity}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartEntity cart);

    @Update("UPDATE user_cart SET quantity = #{quantity}, updated_at = NOW() WHERE id = #{id} AND deleted = 0")
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE user_cart SET deleted = 1, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE user_cart SET deleted = 1, updated_at = NOW() WHERE user_id = #{userId} AND artwork_id = #{artworkId}")
    int deleteByUserAndArtwork(@Param("userId") Long userId, @Param("artworkId") Long artworkId);

    @Select("SELECT COUNT(*) FROM user_cart WHERE user_id = #{userId} AND deleted = 0")
    int countByUserId(@Param("userId") Long userId);
}
