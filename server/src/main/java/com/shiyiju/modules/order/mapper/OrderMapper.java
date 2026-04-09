package com.shiyiju.modules.order.mapper;

import com.shiyiju.modules.order.entity.ArtworkSnapshotEntity;
import com.shiyiju.modules.order.entity.OrderEntity;
import com.shiyiju.modules.order.entity.ShipmentEntity;
import com.shiyiju.modules.order.entity.ShippingAddressEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    ArtworkSnapshotEntity findArtworkSnapshot(@Param("artworkId") Long artworkId);

    ShippingAddressEntity findAddress(@Param("addressId") Long addressId, @Param("userId") Long userId);

    void insertOrder(OrderEntity orderEntity);

    void insertOrderItem(@Param("orderId") Long orderId,
                         @Param("artwork") ArtworkSnapshotEntity artwork,
                         @Param("coverUrl") String coverUrl);

    void insertCertificate(@Param("certificateNo") String certificateNo,
                           @Param("artworkId") Long artworkId,
                           @Param("ownerUserId") Long ownerUserId,
                           @Param("orderId") Long orderId);

    void insertOwnershipFlow(@Param("artworkId") Long artworkId,
                             @Param("toUserId") Long toUserId,
                             @Param("orderId") Long orderId);

    int decreaseArtworkInventory(@Param("artworkId") Long artworkId);

    OrderEntity findOrderDetail(@Param("orderId") Long orderId, @Param("buyerUserId") Long buyerUserId);

    ShippingAddressEntity findOrderAddress(@Param("addressId") Long addressId);

    ShipmentEntity findShipmentByOrderId(@Param("orderId") Long orderId);

    List<OrderEntity> findOrderList(@Param("buyerUserId") Long buyerUserId, 
                                    @Param("status") String status,
                                    @Param("page") Integer page, 
                                    @Param("pageSize") Integer pageSize);
}
