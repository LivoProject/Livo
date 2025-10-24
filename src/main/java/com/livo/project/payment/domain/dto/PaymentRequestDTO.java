package com.livo.project.payment.domain.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String orderId;
    private String orderName;
    private String email;
    private Integer lectureId;
    private Integer reservationId;
    private Integer amount;
}
