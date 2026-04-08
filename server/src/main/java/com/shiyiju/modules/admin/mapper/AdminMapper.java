package com.shiyiju.modules.admin.mapper;

import com.shiyiju.modules.admin.entity.AdminArtistEntity;
import com.shiyiju.modules.admin.entity.AdminArtworkEntity;
import com.shiyiju.modules.admin.entity.AdminOperationEntity;
import com.shiyiju.modules.admin.entity.AdminOrderEntity;
import com.shiyiju.modules.admin.entity.AdminUserEntity;
import com.shiyiju.modules.user.entity.UserAccountEntity;
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

    int insertArtistUser(UserAccountEntity entity);

    int insertArtist(AdminArtistEntity entity);

    int updateArtist(AdminArtistEntity entity);

    int updateArtistStatus(@Param("id") Long id, @Param("status") String status);

    List<AdminArtworkEntity> findArtworks();

    int insertArtwork(AdminArtworkEntity entity);

    int updateArtwork(AdminArtworkEntity entity);

    int updateArtworkStatus(@Param("id") Long id, @Param("status") String status);

    List<AdminUserEntity> findUsers();

    List<AdminOrderEntity> findOrders();

    int updateOrderShipment(@Param("orderId") Long orderId,
                            @Param("company") String company,
                            @Param("trackingNo") String trackingNo);

    int insertOrderShipment(@Param("orderId") Long orderId,
                            @Param("company") String company,
                            @Param("trackingNo") String trackingNo);

    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);

    int updateOrderRemark(@Param("orderId") Long orderId, @Param("remark") String remark);
}
