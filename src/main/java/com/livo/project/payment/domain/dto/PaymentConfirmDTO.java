package com.livo.project.payment.domain.dto;

import lombok.Data;

@Data
public class PaymentConfirmDTO {
    private String paymentKey;
    private String orderId;
    private Integer amount;
    private String email;
    private Integer reservationId;
    private Integer lectureId;

    public PaymentConfirmDTO(String paymentKey, String orderId, Integer amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
