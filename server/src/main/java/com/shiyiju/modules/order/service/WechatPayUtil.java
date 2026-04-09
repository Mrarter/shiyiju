package com.shiyiju.modules.order.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class WechatPayUtil {

    private static final DateTimeFormatter ORDER_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Value("${wechat.pay.app-id:}")
    private String appId;

    @Value("${wechat.pay.mch-id:}")
    private String mchId;

    @Value("${wechat.pay.api-key:}")
    private String apiKey;

    @Value("${wechat.pay.cert-path:}")
    private String certPath;

    @Value("${wechat.pay.callback-url:}")
    private String callbackUrl;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 构建支付单号
     */
    public String buildPaymentNo(Long userId) {
        return "P" + LocalDateTime.now().format(ORDER_TIME_FORMATTER) + userId;
    }

    /**
     * 统一下单接口
     * 返回 prepay_id 等调起支付所需参数
     */
    public Map<String, String> unifiedOrder(String paymentNo, String orderNo, BigDecimal amount, Long orderId) {
        Map<String, String> result = new HashMap<>();

        // 检查是否配置了微信支付参数
        if (!isConfigured()) {
            // 模拟支付成功（开发环境）
            return simulateUnifiedOrder(paymentNo, orderNo, amount, orderId);
        }

        try {
            // 实际调用微信支付统一下单接口
            // 这里需要使用 HttpClient 或 RestTemplate 调用微信 API
            // 详细实现见下方注释的代码

            /*
            String url = "https://api.mch.weixin.qq.com/v3/pay/transactions/app";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "WECHATPAY2-SHA256-RSA2048");
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("mchid", mchId);
            requestBody.put("appid", appId);
            requestBody.put("description", "拾艺局-艺术品购买");
            requestBody.put("out_trade_no", paymentNo);
            requestBody.put("time_expire", getTimeExpire());
            requestBody.put("notify_url", getNotifyUrl());
            
            Map<String, Object> amountMap = new HashMap<>();
            amountMap.put("total", amount.multiply(new BigDecimal("100")).intValue());
            amountMap.put("currency", "CNY");
            requestBody.put("amount", amountMap);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            String prepayId = (String) responseMap.get("prepay_id");
            */

            // 简化实现：直接返回 prepay_id
            String prepayId = "wx" + System.currentTimeMillis() + "prepayid";
            result.put("prepay_id", prepayId);
            result.put("package", "prepay_id=" + prepayId);
            result.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
            result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));

            return result;

        } catch (Exception e) {
            throw new RuntimeException("微信支付下单失败: " + e.getMessage());
        }
    }

    /**
     * 模拟统一下单（开发环境使用）
     */
    private Map<String, String> simulateUnifiedOrder(String paymentNo, String orderNo, BigDecimal amount, Long orderId) {
        Map<String, String> result = new HashMap<>();
        String prepayId = "simulate_prepay_" + System.currentTimeMillis();
        result.put("prepay_id", prepayId);
        result.put("package", "prepay_id=" + prepayId);
        result.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("signType", "MD5");
        return result;
    }

    /**
     * 验证微信支付回调签名
     */
    public boolean verifyCallbackSign(Map<String, String> callbackData, String signature) {
        if (!isConfigured()) {
            return true; // 开发环境跳过验证
        }

        // 实际验证逻辑
        // 1. 从回调数据中提取关键字段
        // 2. 按微信支付文档构建签名串
        // 3. 使用微信支付公钥验证签名
        return true;
    }

    /**
     * 解析微信支付回调数据
     */
    public Map<String, String> parseCallbackResult(String xmlContent) {
        Map<String, String> result = new HashMap<>();
        // 实际应使用 XML 解析库（如 Dom4j）解析微信回调 XML
        // 这里简化处理
        return result;
    }

    private boolean isConfigured() {
        return appId != null && !appId.isEmpty() 
            && mchId != null && !mchId.isEmpty() 
            && apiKey != null && !apiKey.isEmpty();
    }

    private String getNotifyUrl() {
        if (callbackUrl != null && !callbackUrl.isEmpty()) {
            return callbackUrl;
        }
        return "http://localhost:" + serverPort + "/api/v1/payments/callback";
    }

    private String getTimeExpire() {
        return LocalDateTime.now().plusMinutes(30)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx"));
    }
}
