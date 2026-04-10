package com.shiyiju.modules.order.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.order.dto.CreateOrderDTO;
import com.shiyiju.modules.order.service.OrderService;
import com.shiyiju.modules.order.vo.OrderDetailVO;
import com.shiyiju.modules.order.vo.OrderListVO;
import com.shiyiju.security.CurrentUserHolder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<OrderDetailVO> create(@Valid @RequestBody CreateOrderDTO request) {
        return ApiResponse.success("下单成功", orderService.createOrder(CurrentUserHolder.get(), request));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable("id") Long orderId) {
        return ApiResponse.success(orderService.getOrderDetail(CurrentUserHolder.get(), orderId));
    }

    /**
     * 获取订单列表
     */
    @GetMapping
    public ApiResponse<List<OrderListVO>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(orderService.getOrderList(CurrentUserHolder.get(), status, page, pageSize));
    }
}
