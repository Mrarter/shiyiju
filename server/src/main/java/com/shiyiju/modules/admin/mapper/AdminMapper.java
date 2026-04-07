package com.shiyiju.modules.admin.mapper;

import com.shiyiju.modules.admin.entity.AdminArtistEntity;
import com.shiyiju.modules.admin.entity.AdminArtworkEntity;
import com.shiyiju.modules.admin.entity.AdminOrderEntity;
import com.shiyiju.modules.admin.entity.AdminUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {

    List<AdminArtistEntity> findArtists();

    List<AdminArtworkEntity> findArtworks();

    List<AdminUserEntity> findUsers();

    List<AdminOrderEntity> findOrders();
}
