package com.livo.project.payment.controller;

import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.payment.domain.Payment;
import com.livo.project.payment.domain.dto.PaymentConfirmDTO;
import com.livo.project.payment.domain.dto.PaymentRequestDTO;
import com.livo.project.payment.repository.PaymentRepository;
import com.livo.project.payment.service.PaymentService;
import com.livo.project.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/ready")
    public ResponseEntity<?> readyPayment(@RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(paymentService.readyPayment(dto));
    }

    @GetMapping("/confirm")
    public String success(@RequestParam String paymentKey,
                          @RequestParam String orderId,
                          @RequestParam int amount,
                          @RequestParam int reservationId,
                          @RequestParam int lectureId,
                          RedirectAttributes redirectAttributes) {
        String email = AuthUtil.getLoginUserEmail();
        PaymentConfirmDTO dto = new PaymentConfirmDTO();
        dto.setPaymentKey(paymentKey);
        dto.setOrderId(orderId);
        dto.setAmount(amount);
        dto.setReservationId(reservationId);
        dto.setLectureId(lectureId);
        dto.setEmail(email);
        Map<String, Object> result = paymentService.confirmPayment(dto);
        redirectAttributes.addFlashAttribute("result", result);
        return "redirect:/payment/success";
    }

    @GetMapping("/success")
    public String success(){
        return "payment/success";
    }

    @PostMapping("/cancel")
    @ResponseBody
    public Map<String, Object> cancel(@RequestParam String paymentKey){
        return paymentService.cancelPayment(paymentKey, "사용자 요청 환불");
    }
    //환불 테스트용 리스트
    @GetMapping("/list")
    public String paymentList(Model model){
        String email = AuthUtil.getLoginUserEmail();
        List<Payment> payments = paymentRepository.findByUser_EmailOrderByApprovedAtDesc(email);
        model.addAttribute("payments", payments);
        return "payment/list";
    }
}
