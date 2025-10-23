package com.livo.project.payment.controller;

import com.livo.project.payment.domain.dto.PaymentConfirmDTO;
import com.livo.project.payment.domain.dto.PaymentRequestDTO;
import com.livo.project.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<?> readyPayment(@RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(paymentService.readyPayment(dto));
    }
    //결제 승인(Toss redirect 이후 호출)
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmDTO dto) {
        return ResponseEntity.ok(paymentService.confirmPayment(dto));
    }
}
