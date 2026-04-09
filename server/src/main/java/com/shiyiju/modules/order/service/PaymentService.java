package com.shiyiju.modules.order.service;

import com.shiyiju.modules.order.entity.PaymentRecordEntity;
import com.shiyiju.modules.order.mapper.PaymentMapper;
import com.shiyiju.modules.order.vo.PrepayVO;
import com.shiyiju.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private static final int PAYMENT_EXPIRE_MINUTES = 30;

    private final PaymentMapper paymentMapper;
    private final OrderQueryService orderQueryService;
    private final WechatPayUtil wechatPayUtil;

    public PaymentService(PaymentMapper paymentMapper, OrderQueryService orderQueryService, WechatPayUtil wechatPayUtil) {
        this.paymentMapper = paymentMapper;
        this.orderQueryService = orderQueryService;
        this.wechatPayUtil = wechatPayUtil;
    }

    @Transactional
    public PrepayVO prepay(Long currentUserId, Long orderId) {
        var order = orderQueryService.getOrderForPayment(currentUserId, orderId);

        if (!"UNPAID".equals(order.getPaymentStatus())) {
            throw new BusinessException(40010, "订单已支付或状态异常");
        }

        // 检查支付过期时间
        LocalDateTime createdAt = order.getCreatedAt();
        LocalDateTime expireTime = createdAt.plusMinutes(PAYMENT_EXPIRE_MINUTES);
        if (LocalDateTime.now().isAfter(expireTime)) {
            throw new BusinessException(40011, "订单已过期，请重新下单");
        }

        // 创建支付记录
        String paymentNo = wechatPayUtil.buildPaymentNo(currentUserId);
        PaymentRecordEntity paymentRecord = new PaymentRecordEntity();
        paymentRecord.setOrderId(orderId);
        paymentRecord.setPaymentNo(paymentNo);
        paymentRecord.setPaymentChannel("WECHAT_PAY");
        paymentRecord.setAmount(order.getPayAmount());
        paymentRecord.setPaymentStatus("PENDING");
        paymentMapper.insertPaymentRecord(paymentRecord);

        // 调用微信支付统一下单
        Map<String, String> payResult = wechatPayUtil.unifiedOrder(
            paymentNo,
            order.getOrderNo(),
            order.getPayAmount(),
            orderId
        );

        // 更新支付记录
        paymentMapper.updatePaymentPrepayId(paymentNo, payResult.get("prepay_id"));

        // 返回小程序调起支付需要的参数
        PrepayVO vo = new PrepayVO();
        vo.setPrepayId(payResult.get("prepay_id"));
        vo.setOrderNo(order.getOrderNo());
        vo.setPayAmount(order.getPayAmount());
        vo.setPayStatus("PENDING");
        vo.setExpiresAt(expireTime.toEpochSecond(ZoneOffset.of("+8")) * 1000);
        return vo;
    }

    @Transactional
    public void handlePayCallback(String paymentNo, String transactionNo) {
        PaymentRecordEntity payment = paymentMapper.findByPaymentNo(paymentNo);
        if (payment == null) {
            throw new BusinessException(40012, "支付记录不存在");
        }

        if ("SUCCESS".equals(payment.getPaymentStatus())) {
            return; // 避免重复处理
        }

        // 更新支付记录
        paymentMapper.updatePaymentSuccess(paymentNo, transactionNo);

        // 更新订单状态
        paymentMapper.updateOrderPaid(payment.getOrderId());

        // 订单后续处理（发货单等）
        paymentMapper.insertShipmentOrder(payment.getOrderId());
    }

    @Transactional
    public void handleCallback(String xmlData) {
        // 解析微信支付回调 XML
        Map<String, String> data = wechatPayUtil.parseCallbackResult(xmlData);
        
        String returnCode = data.get("return_code");
        if (!"SUCCESS".equals(returnCode)) {
            throw new BusinessException(40014, "微信支付返回失败");
        }

        String paymentNo = data.get("out_trade_no");
        String transactionNo = data.get("transaction_id");

        handlePayCallback(paymentNo, transactionNo);
    }

    /**
     * 模拟支付成功（开发环境测试用）
     */
    @Transactional
    public void simulatePaySuccess(Long orderId) {
        var order = orderQueryService.getOrderForPayment(null, orderId);
        String paymentNo = wechatPayUtil.buildPaymentNo(order.getBuyerUserId());
        
        PaymentRecordEntity payment = new PaymentRecordEntity();
        payment.setOrderId(orderId);
        payment.setPaymentNo(paymentNo);
        payment.setPaymentChannel("WECHAT_PAY");
        payment.setAmount(order.getPayAmount());
        payment.setPaymentStatus("SUCCESS");
        paymentMapper.insertPaymentRecord(payment);

        paymentMapper.updatePaymentSuccess(paymentNo, "SIMULATE_" + System.currentTimeMillis());
        paymentMapper.updateOrderPaid(orderId);
        paymentMapper.insertShipmentOrder(orderId);
    }

    @Transactional
    public void cancelOrder(Long currentUserId, Long orderId) {
        var order = orderQueryService.getOrderForPayment(currentUserId, orderId);

        if (!"UNPAID".equals(order.getPaymentStatus())) {
            throw new BusinessException(40013, "只有待支付订单可以取消");
        }

        // 更新订单状态
        paymentMapper.updateOrderCancelled(orderId);

        // 回滚库存
        paymentMapper.restoreArtworkInventory(orderId);

        // 删除未使用的支付记录
        paymentMapper.deletePendingPayment(orderId);
    }

    public Map<String, Object> getPaymentStatus(Long currentUserId, Long orderId) {
        var order = orderQueryService.getOrderForPayment(currentUserId, orderId);
        PaymentRecordEntity payment = paymentMapper.findLatestByOrderId(orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderNo", order.getOrderNo());
        result.put("paymentStatus", order.getPaymentStatus());
        result.put("orderStatus", order.getOrderStatus());
        result.put("payAmount", order.getPayAmount());

        if (payment != null) {
            result.put("paymentNo", payment.getPaymentNo());
            result.put("transactionNo", payment.getTransactionNo());
            result.put("paidAt", payment.getPaidAt());
        }

        return result;
    }
}
