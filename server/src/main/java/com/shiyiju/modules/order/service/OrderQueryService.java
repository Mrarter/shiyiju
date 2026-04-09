package com.shiyiju.modules.order.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.order.entity.OrderEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderQueryService {

    private final com.shiyiju.modules.order.mapper.OrderMapper orderMapper;

    public OrderQueryService(com.shiyiju.modules.order.mapper.OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public OrderEntity getOrderForPayment(Long currentUserId, Long orderId) {
        OrderEntity order = orderMapper.findOrderDetail(orderId, currentUserId);
        if (order == null) {
            throw new BusinessException(40008, "订单不存在");
        }
        return order;
    }
}
