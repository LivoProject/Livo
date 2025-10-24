package com.livo.project.payment.service;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import com.livo.project.lecture.domain.Reservation;
import com.livo.project.lecture.repository.ReservationRepository;
import com.livo.project.payment.domain.Payment;
import com.livo.project.payment.domain.dto.PaymentConfirmDTO;
import com.livo.project.payment.domain.dto.PaymentRequestDTO;
import com.livo.project.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Value("${toss.secret-key}")
    private String secretKey;

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/confirm";

    // 결제 준비
    public Map<String, Object> readyPayment(PaymentRequestDTO dto) {
        Map<String, Object> res = new HashMap<>();
        res.put("orderId", dto.getOrderId());
        res.put("orderName", dto.getOrderName());
        res.put("amount", dto.getAmount());
        res.put("message", "토스 결제 요청 준비 완료");
        return res;
    }

    // 결제 승인
    @Transactional
    public Map<String, Object> confirmPayment(PaymentConfirmDTO dto) {
        Map<String, Object> res = new HashMap<>();
        log.info("💳 [TOSS 결제 승인 요청] orderId={}, paymentKey={}, amount={}",
                dto.getOrderId(), dto.getPaymentKey(), dto.getAmount());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("🔑 secretKey=[{}]", secretKey);
        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", dto.getPaymentKey());
        body.put("orderId", dto.getOrderId());
        body.put("amount", dto.getAmount());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(TOSS_API_URL, request, Map.class);
            log.info("✅ [TOSS 응답] status={}, body={}", response.getStatusCode(), response.getBody());
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                String orderName = (String) responseBody.get("orderName");
                String method = (String) responseBody.get("method");

                //db의 로그인 유저 이메일 기준
                User user = userRepository.findByEmail(dto.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));
                Reservation reservation = reservationRepository.findByReservationId(dto.getReservationId())
                        .orElseThrow(() -> new IllegalArgumentException("예약 정보 없음"));
                // Payment 저장
                Payment payment = new Payment();
                payment.setPaymentKey(dto.getPaymentKey());
                payment.setOrderId(dto.getOrderId());
                payment.setOrderName(orderName);
                payment.setUser(user);
                payment.setAmount(dto.getAmount());
                payment.setMethod(method);
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setApprovedAt(LocalDateTime.now());
                payment.setReservation(reservation);
                paymentRepository.save(payment);

                // 예약 상태 변경
                reservation.setStatus(Reservation.ReservationStatus.PAID);
                reservationRepository.save(reservation);

                res.put("status", "SUCCESS");
                res.put("message", "결제 승인 및 DB 저장 완료");
            } else {
                res.put("status", "FAIL");
                res.put("message", "Toss 결제 승인 실패");
            }

        } catch (Exception e) {
            log.error("❌ [TOSS 결제 승인 실패]", e);
            res.put("status", "FAIL");
            res.put("error", e.getMessage());
        }

        return res;
    }
}
