package com.shiyiju.modules.order.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.order.dto.CreateOrderDTO;
import com.shiyiju.modules.order.entity.ArtworkSnapshotEntity;
import com.shiyiju.modules.order.entity.OrderEntity;
import com.shiyiju.modules.order.entity.ShipmentEntity;
import com.shiyiju.modules.order.entity.ShippingAddressEntity;
import com.shiyiju.modules.order.mapper.OrderMapper;
import com.shiyiju.modules.order.vo.OrderAddressVO;
import com.shiyiju.modules.order.vo.OrderDetailVO;
import com.shiyiju.modules.order.vo.OrderItemVO;
import com.shiyiju.modules.order.vo.ShipmentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderService {

    private static final DateTimeFormatter ORDER_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderMapper orderMapper;

    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderDetailVO createOrder(Long currentUserId, CreateOrderDTO request) {
        ArtworkSnapshotEntity artwork = orderMapper.findArtworkSnapshot(request.getArtworkId());
        if (artwork == null) {
            throw new BusinessException(40004, "作品不存在");
        }
        if (!"PUBLISHED".equals(artwork.getStatus())) {
            throw new BusinessException(40005, "当前作品暂不可下单");
        }
        if (artwork.getInventoryAvailable() == null || artwork.getInventoryAvailable() < 1) {
            throw new BusinessException(40006, "作品库存不足");
        }

        ShippingAddressEntity address = orderMapper.findAddress(request.getAddressId(), currentUserId);
        if (address == null) {
            throw new BusinessException(40007, "收货地址不存在");
        }

        int updated = orderMapper.decreaseArtworkInventory(artwork.getArtworkId());
        if (updated < 1) {
            throw new BusinessException(40006, "作品已被抢先下单");
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(buildOrderNo(currentUserId));
        orderEntity.setBuyerUserId(currentUserId);
        orderEntity.setOrderType("PRIMARY");
        orderEntity.setOrderStatus("PENDING_PAYMENT");
        orderEntity.setPaymentStatus("UNPAID");
        orderEntity.setDeliveryType("ARTWORK");
        orderEntity.setAddressId(address.getId());
        orderEntity.setGoodsAmount(artwork.getCurrentPrice());
        orderEntity.setFreightAmount(BigDecimal.ZERO);
        orderEntity.setDiscountAmount(BigDecimal.ZERO);
        orderEntity.setPayAmount(artwork.getCurrentPrice());
        orderEntity.setRemark(normalizeRemark(request.getRemark()));
        orderMapper.insertOrder(orderEntity);
        orderMapper.insertOrderItem(orderEntity.getOrderId(), artwork, artwork.getCoverUrl());
        orderMapper.insertCertificate(buildCertificateNo(currentUserId), artwork.getArtworkId(), currentUserId, orderEntity.getOrderId());
        orderMapper.insertOwnershipFlow(artwork.getArtworkId(), currentUserId, orderEntity.getOrderId());
        return getOrderDetail(currentUserId, orderEntity.getOrderId());
    }

    public OrderDetailVO getOrderDetail(Long currentUserId, Long orderId) {
        OrderEntity entity = orderMapper.findOrderDetail(orderId, currentUserId);
        if (entity == null) {
            throw new BusinessException(40008, "订单不存在");
        }

        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrderId(entity.getOrderId());
        vo.setOrderNo(entity.getOrderNo());
        vo.setOrderType(entity.getOrderType());
        vo.setOrderStatus(entity.getOrderStatus());
        vo.setPaymentStatus(entity.getPaymentStatus());
        vo.setDeliveryType(entity.getDeliveryType());
        vo.setGoodsAmount(entity.getGoodsAmount());
        vo.setFreightAmount(entity.getFreightAmount());
        vo.setDiscountAmount(entity.getDiscountAmount());
        vo.setPayAmount(entity.getPayAmount());
        vo.setRemark(entity.getRemark());
        vo.setPaidAt(entity.getPaidAt());
        vo.setCompletedAt(entity.getCompletedAt());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setAddress(buildAddress(orderMapper.findOrderAddress(entity.getAddressId())));
        vo.setItem(buildItem(entity));
        vo.setShipment(buildShipment(orderMapper.findShipmentByOrderId(orderId)));
        return vo;
    }

    private OrderAddressVO buildAddress(ShippingAddressEntity entity) {
        if (entity == null) {
            return null;
        }
        OrderAddressVO vo = new OrderAddressVO();
        vo.setAddressId(entity.getId());
        vo.setReceiverName(entity.getReceiverName());
        vo.setReceiverMobile(entity.getReceiverMobile());
        vo.setProvince(entity.getProvince());
        vo.setCity(entity.getCity());
        vo.setDistrict(entity.getDistrict());
        vo.setDetailAddress(entity.getDetailAddress());
        return vo;
    }

    private OrderItemVO buildItem(OrderEntity entity) {
        OrderItemVO vo = new OrderItemVO();
        vo.setArtworkId(entity.getArtworkId());
        vo.setArtistId(entity.getArtistId());
        vo.setItemTitle(entity.getItemTitle());
        vo.setCoverUrl(entity.getCoverUrl());
        vo.setUnitPrice(entity.getUnitPrice());
        vo.setQuantity(entity.getQuantity());
        vo.setSubtotalAmount(entity.getSubtotalAmount());
        return vo;
    }

    private ShipmentVO buildShipment(ShipmentEntity entity) {
        if (entity == null) {
            return null;
        }
        ShipmentVO vo = new ShipmentVO();
        vo.setShipmentNo(entity.getShipmentNo());
        vo.setLogisticsCompany(entity.getLogisticsCompany());
        vo.setLogisticsCode(entity.getLogisticsCode());
        vo.setTrackingNo(entity.getTrackingNo());
        vo.setShipmentStatus(entity.getShipmentStatus());
        vo.setConsignedAt(entity.getConsignedAt());
        vo.setSignedAt(entity.getSignedAt());
        return vo;
    }

    private String buildOrderNo(Long currentUserId) {
        return "O" + LocalDateTime.now().format(ORDER_TIME_FORMATTER) + currentUserId;
    }

    private String buildCertificateNo(Long currentUserId) {
        return "CERT" + LocalDateTime.now().format(ORDER_TIME_FORMATTER) + currentUserId;
    }

    private String normalizeRemark(String remark) {
        return StringUtils.hasText(remark) ? remark.trim() : null;
    }
}
