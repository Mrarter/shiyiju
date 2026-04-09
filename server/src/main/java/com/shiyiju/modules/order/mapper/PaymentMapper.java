package com.shiyiju.modules.order.mapper;

import com.shiyiju.modules.order.entity.PaymentRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    void insertPaymentRecord(@Param("payment") PaymentRecordEntity payment);

    PaymentRecordEntity findByPaymentNo(@Param("paymentNo") String paymentNo);

    PaymentRecordEntity findLatestByOrderId(@Param("orderId") Long orderId);

    void updatePaymentPrepayId(@Param("paymentNo") String paymentNo, @Param("prepayId") String prepayId);

    void updatePaymentSuccess(@Param("paymentNo") String paymentNo, @Param("transactionNo") String transactionNo);

    void deletePendingPayment(@Param("orderId") Long orderId);

    void updateOrderPaid(@Param("orderId") Long orderId);

    void updateOrderCancelled(@Param("orderId") Long orderId);

    void restoreArtworkInventory(@Param("orderId") Long orderId);

    void insertShipmentOrder(@Param("orderId") Long orderId);
}
