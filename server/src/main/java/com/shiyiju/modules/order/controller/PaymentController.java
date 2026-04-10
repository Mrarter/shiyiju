package com.shiyiju.modules.order.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.order.dto.PrepayDTO;
import com.shiyiju.modules.order.service.PaymentService;
import com.shiyiju.modules.order.vo.PrepayVO;
import com.shiyiju.security.CurrentUserHolder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 预支付：获取调起微信支付所需的参数
     */
    @PostMapping("/prepay")
    public ApiResponse<PrepayVO> prepay(@Valid @RequestBody PrepayDTO request) {
        return ApiResponse.success(paymentService.prepay(CurrentUserHolder.get(), request.getOrderId()));
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public ApiResponse<String> cancelOrder(@PathVariable Long orderId) {
        paymentService.cancelOrder(CurrentUserHolder.get(), orderId);
        return ApiResponse.success("订单已取消", null);
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{orderId}")
    public ApiResponse<Map<String, Object>> getPaymentStatus(@PathVariable Long orderId) {
        return ApiResponse.success(paymentService.getPaymentStatus(CurrentUserHolder.get(), orderId));
    }

    /**
     * 微信支付回调（需要配置公网可访问的回调地址）
     */
    @PostMapping("/callback")
    public String paymentCallback(@RequestBody String xmlData) {
        try {
            paymentService.handleCallback(xmlData);
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
        } catch (Exception e) {
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[" + e.getMessage() + "]]></return_msg></xml>";
        }
    }

    /**
     * 模拟支付成功（开发环境测试用）
     */
    @PostMapping("/simulate")
    public ApiResponse<String> simulatePay(@RequestBody Map<String, Long> body) {
        Long orderId = body.get("orderId");
        if (orderId == null) {
            return ApiResponse.fail(40001, "订单ID不能为空");
        }
        paymentService.simulatePaySuccess(orderId);
        return ApiResponse.success("模拟支付成功", null);
    }
}
