package com.shiyiju.modules.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRecordEntity {

    private Long id;
    private Long orderId;
    private String paymentNo;
    private String paymentChannel;
    private String transactionNo;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private LocalDateTime callbackAt;
    private String rawCallback;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getPaymentNo() { return paymentNo; }
    public void setPaymentNo(String paymentNo) { this.paymentNo = paymentNo; }
    public String getPaymentChannel() { return paymentChannel; }
    public void setPaymentChannel(String paymentChannel) { this.paymentChannel = paymentChannel; }
    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getCallbackAt() { return callbackAt; }
    public void setCallbackAt(LocalDateTime callbackAt) { this.callbackAt = callbackAt; }
    public String getRawCallback() { return rawCallback; }
    public void setRawCallback(String rawCallback) { this.rawCallback = rawCallback; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
