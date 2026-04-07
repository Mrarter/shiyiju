package com.shiyiju.modules.admin.mapper;

import com.shiyiju.modules.admin.entity.AdminArtistEntity;
import com.shiyiju.modules.admin.entity.AdminArtworkEntity;
import com.shiyiju.modules.admin.entity.AdminOperationEntity;
import com.shiyiju.modules.admin.entity.AdminOrderEntity;
import com.shiyiju.modules.admin.entity.AdminUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {

    List<AdminOperationEntity> findOperations();

    int insertOperation(AdminOperationEntity entity);

    int updateOperation(AdminOperationEntity entity);

    int updateOperationStatus(@Param("id") Long id, @Param("status") String status);

    List<AdminArtistEntity> findArtists();

    int updateArtistStatus(@Param("id") Long id, @Param("status") String status);

    List<AdminArtworkEntity> findArtworks();

    int updateArtworkStatus(@Param("id") Long id, @Param("status") String status);

    List<AdminUserEntity> findUsers();

    List<AdminOrderEntity> findOrders();
}
