package com.shiyiju.modules.order.dto;

import jakarta.validation.constraints.NotNull;

public class PrepayDTO {

    @NotNull
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
