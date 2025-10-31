package com.livo.project.payment.service;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import com.livo.project.lecture.repository.LectureRepository;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureLectureRepository;

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
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", dto.getPaymentKey());
        body.put("orderId", dto.getOrderId());
        body.put("amount", dto.getAmount());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(TOSS_API_URL, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                String orderName = (String) responseBody.get("orderName");
                String method = (String) responseBody.get("method");
                Integer amount = (Integer) responseBody.get("totalAmount");
                String approvedAt = (String) responseBody.get("approvedAt");

                //db의 로그인 유저 이메일 기준
                User user = userRepository.findByEmail(dto.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));
                Reservation reservation = reservationRepository.findByReservationId(dto.getReservationId())
                        .orElseThrow(() -> new IllegalArgumentException("예약 정보 없음"));
                Lecture lecture = lectureLectureRepository.findByLectureId(dto.getLectureId())
                        .orElseThrow(() -> new IllegalArgumentException("강의 정보 없음"));
                // Payment 저장
                Payment payment = new Payment();
                payment.setPaymentKey(dto.getPaymentKey());
                payment.setOrderId(dto.getOrderId());
                payment.setOrderName(orderName);
                payment.setUser(user);
                payment.setAmount(amount);
                payment.setMethod(method);
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setApprovedAt(LocalDateTime.now());
                payment.setReservation(reservation);
                payment.setLecture(lecture);
                paymentRepository.save(payment);

                // 예약 상태 변경
                reservation.setStatus(Reservation.ReservationStatus.PAID);
                reservationRepository.save(reservation);
                //신청인원 증가
                lecture.setReservationCount(lecture.getReservationCount() + 1);
                lectureLectureRepository.save(lecture);

                res.put("status", "SUCCESS");
                res.put("orderName", orderName);
                res.put("amount", amount);
                res.put("orderId", dto.getOrderId());
                res.put("paymentKey", dto.getPaymentKey());
                res.put("method", method);
                res.put("approvedAt", approvedAt);
            } else {
                res.put("status", "FAIL");
                res.put("message", "Toss 결제 승인 실패");
            }

        }catch(HttpClientErrorException e) {
            if(e.getMessage().contains("ALREADY_PROCESSED_PAYMENT")){
                syncPaidPayment(dto.getPaymentKey());
                res.put("status", "SUCCESS");
                res.put("message", "이미 처리된 결제였으며, DB 상태를 동기화했습니다.");
                return res;
            }
            res.put("status", "FAIL");
            res.put("error", e.getMessage());
        }
        catch (Exception e) {
            res.put("status", "FAIL");
            res.put("error", e.getMessage());
        }

        return res;
    }
    @Transactional
    public Map<String, Object> cancelPayment(String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보 없음"));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", cancelReason);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String url = "https://api.tosspayments.com/v1/payments/"+paymentKey+"/cancel";

        Map<String, Object> res = new HashMap<>();

        try{
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if(response.getStatusCode() == HttpStatus.OK){
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> cancels = (List<Map<String, Object>>) responseBody.get("cancels");
                String canceledAtStr = (String) cancels.get(0).get("canceledAt");
                OffsetDateTime canceledDate = OffsetDateTime.parse(canceledAtStr);
                payment.setStatus(Payment.PaymentStatus.REFUND);
                payment.setCanceledAt(canceledDate.toLocalDateTime());
                paymentRepository.save(payment);

                Reservation reservation = payment.getReservation();
                boolean wasActive = reservation.getStatus() == Reservation.ReservationStatus.PAID;

                reservation.setStatus(Reservation.ReservationStatus.CANCEL);
                reservationRepository.save(reservation);
                if(wasActive){
                    Lecture lecture = lectureLectureRepository.findById(reservation.getLectureId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 강의없음"));
                    lecture.setReservationCount(Math.max(lecture.getReservationCount() - 1, 0));
                    lectureLectureRepository.save(lecture);
                }
                res.put("status", "SUCCESS");
            }else {
                res.put("status", "FAIL");
            }
        } catch (HttpClientErrorException e) {
            if (e.getMessage().contains("ALREADY_CANCELED_PAYMENT")) {
                syncCanceledPayment(paymentKey);
                res.put("status", "SUCCESS");
                res.put("message", "이미 취소된 결제였으며, DB 상태를 동기화했습니다.");
                return res;
            }
            res.put("status", "FAIL");
            res.put("error", e.getMessage());
        } catch (Exception e){
            res.put("status", "FAIL");
            res.put("error", e.getMessage());
        }
        return res;
    }
    //토스 결제 단건 조회 API로 조회
    public Map<String, Object> getPaymentDetail(String paymentKey) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.tosspayments.com/v1/payments/" + paymentKey,
                HttpMethod.GET,
                request,
                Map.class
        );

        return response.getBody();
    }
    //DB 상태를 싱크
    @Transactional
    public void syncPaidPayment(String paymentKey) {
        Map<String, Object> detail = getPaymentDetail(paymentKey);

        String orderName = (String) detail.get("orderName");
        Integer amount = (Integer) detail.get("totalAmount");
        String method = (String) detail.get("method");
        String approvedAt = (String) detail.get("approvedAt");

        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("해당 paymentKey로 저장된 결제가 없습니다."));
        Reservation reservation = payment.getReservation();
        if(reservation == null){
            throw new IllegalArgumentException("paymentKey로 연결된 예약 정보가 없습니다.");
        }
        Lecture lecture = reservation.getLecture();
        User user = reservation.getUser();

        // Payment 새로 기록
        payment.setOrderName(orderName);
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setApprovedAt(LocalDateTime.parse(approvedAt));
        payment.setReservation(reservation);
        payment.setLecture(lecture);
        paymentRepository.save(payment);

        // 예약 상태 PAID로
        reservation.setStatus(Reservation.ReservationStatus.PAID);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void syncCanceledPayment(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보 없음"));

        Map<String, Object> detail = getPaymentDetail(paymentKey);
        if ("CANCELED".equals(detail.get("status"))) {
            List<Map<String, Object>> cancels = (List<Map<String, Object>>) detail.get("cancels");
            if (cancels != null && !cancels.isEmpty()) {
                String canceledAtStr = (String) cancels.get(0).get("canceledAt");
                OffsetDateTime canceledDate = OffsetDateTime.parse(canceledAtStr);
                payment.setStatus(Payment.PaymentStatus.REFUND);
                payment.setCanceledAt(canceledDate.toLocalDateTime());
                paymentRepository.save(payment);

                Reservation reservation = payment.getReservation();
                reservation.setStatus(Reservation.ReservationStatus.CANCEL);
                reservationRepository.save(payment.getReservation());
            }
        }
    }


}
