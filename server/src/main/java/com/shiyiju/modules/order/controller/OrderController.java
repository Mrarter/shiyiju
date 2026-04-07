package com.shiyiju.modules.order.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.order.dto.CreateOrderDTO;
import com.shiyiju.modules.order.service.OrderService;
import com.shiyiju.modules.order.vo.OrderDetailVO;
import com.shiyiju.security.CurrentUserHolder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderDetailVO> create(@Valid @RequestBody CreateOrderDTO request) {
        return ApiResponse.success("下单成功", orderService.createOrder(CurrentUserHolder.get(), request));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable("id") Long orderId) {
        return ApiResponse.success(orderService.getOrderDetail(CurrentUserHolder.get(), orderId));
    }
}
