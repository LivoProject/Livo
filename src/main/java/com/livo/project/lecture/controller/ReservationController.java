package com.livo.project.lecture.controller;

import com.livo.project.lecture.service.ReservationService;
import com.livo.project.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
@Controller
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/create/{lectureId}")
    @ResponseBody
    public Map<String, Object> createReservation(@PathVariable int lectureId) {
        String email = AuthUtil.getLoginUserEmail();

        if (email == null) {
            return Map.of("success", false, "message", "로그인 후 사용 가능합니다.");
        }
        int reservationId = reservationService.createPendingReservation(lectureId, email);
        log.info("📘 예약 생성 요청: lectureId={}, email={}", lectureId, email);
        return Map.of("success", true, "reservationId", reservationId);
    }
}
