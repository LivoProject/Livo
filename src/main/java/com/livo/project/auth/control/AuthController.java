// src/main/java/com/livo/project/auth/control/AuthController.java
package com.livo.project.auth.control;

import com.livo.project.auth.domain.dto.SignUpRequest;
import com.livo.project.auth.service.BusinessException;
import com.livo.project.auth.service.EmailVerificationService;
import com.livo.project.auth.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    /** 모든 String 필드 앞뒤 공백 제거 + 빈 문자열을 null 로 변환 */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /** 회원가입 폼 (GET) — 세션에 VERIFIED_EMAIL 있으면 이메일 입력 잠금 */
    @GetMapping("/register")
    public String registerForm(@ModelAttribute("signUpRequest") SignUpRequest form,
                               @SessionAttribute(value = "VERIFIED_EMAIL", required = false) String verifiedEmail,
                               Model model) {
        log.info("[REGISTER:GET] 회원가입 폼 진입 - VERIFIED_EMAIL={}", verifiedEmail);
        if (verifiedEmail != null) model.addAttribute("verifiedEmail", verifiedEmail);
        return "auth/register";
    }

    // ─────────────────────────────────────────────────────
    // 1) 이메일 인증 코드 (AJAX)
    // ─────────────────────────────────────────────────────

    /** 인증 코드 전송 */
    @PostMapping(
            value = "/send-code",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> sendCode(@RequestParam("email") String email) {
        log.info("[SEND-CODE] 요청 email={}", email);
        var r = emailVerificationService.sendCode(email);

        if (r.ok()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", r.message(),
                    "cooldownRemainSec", r.cooldownRemainSec()
            ));
        }
        return ResponseEntity.status(429).body(Map.of(
                "success", false,
                "message", r.message(),
                "cooldownRemainSec", r.cooldownRemainSec()
        ));
    }

    /** 인증 코드 검증 → 성공 시 세션에 VERIFIED_EMAIL 저장 */
    @PostMapping(
            value = "/verify-code",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> verifyCode(@RequestParam("email") String email,
                                        @RequestParam("code") String code,
                                        HttpSession session) {
        log.info("[VERIFY-CODE] email={}, code=******", email);
        var r = emailVerificationService.verifyCode(email, code);

        if (r.ok()) {
            //  이메일 인증 성공: 세션에 상태 저장
            session.setAttribute("VERIFIED_EMAIL", email.toLowerCase());

            //  이 플로우에서만 세션 유지시간 연장(예: 10분)
            session.setMaxInactiveInterval(10 * 60);

            return ResponseEntity.ok(Map.of("success", true, "message", r.message()));
        }
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", r.message()));
    }
    // ─────────────────────────────────────────────────────
    // 2) 회원가입 처리 (AJAX/JSON 전용)
    // ─────────────────────────────────────────────────────

    /** JSON 회원가입 — 성공 시 {"success": true} 반환 */
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> registerJson(@Valid @RequestBody SignUpRequest form,
                                          @SessionAttribute(value = "VERIFIED_EMAIL", required = false) String verifiedEmail,
                                          HttpSession session) {

        log.info("[REGISTER:AJAX] JSON 회원가입 요청 email={}, nickname={}", form.getEmail(), form.getNickname());
        log.info("[REGISTER:AJAX] 세션 VERIFIED_EMAIL={}", verifiedEmail);

        // 이메일 미인증 차단
        if (verifiedEmail == null || !verifiedEmail.equalsIgnoreCase(form.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("email", "이메일 인증 후 진행해 주세요."));
        }

        try {
            userService.register(form);                // 실제 저장
            session.removeAttribute("VERIFIED_EMAIL"); // 세션 정리
            return ResponseEntity.ok(Map.of("success", true));

        } catch (BusinessException ex) {              // 서비스 레벨 비즈니스 예외 → 4xx
            String key = (ex.getField() == null) ? "error" : ex.getField();
            String msg = (ex.getMessage() == null) ? "요청을 처리할 수 없습니다." : ex.getMessage();
            return ResponseEntity.status(
                    ex.getStatus() != null ? ex.getStatus() : org.springframework.http.HttpStatus.BAD_REQUEST
            ).body(Map.of(key, msg));

        } catch (IllegalArgumentException e) {        // 문자열 "field: message" 패턴 처리
            String msg = Optional.ofNullable(e.getMessage()).orElse("");
            int i = msg.indexOf(':');
            if (i > 0) {
                String field = msg.substring(0, i).trim();
                String text  = msg.substring(i + 1).trim();
                return ResponseEntity.badRequest().body(Map.of(field, text));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "요청을 처리할 수 없습니다."));

        } catch (DataIntegrityViolationException e) { // DB 유니크 제약 처리
            String root = Optional.ofNullable(e.getMostSpecificCause()).map(Throwable::getMessage).orElse("");
            Map<String, String> err = new HashMap<>();
            if (root.contains("uk_user_email")) err.put("email", "이미 등록된 이메일입니다.");
            else if (root.contains("uq_user_nickname")) err.put("nickname", "이미 사용 중인 닉네임입니다.");
            else if (root.contains("uq_user_phone")) err.put("phone", "이미 등록된 전화번호입니다.");
            else err.put("error", "이미 사용 중인 정보가 있습니다.");
            return ResponseEntity.badRequest().body(err);

        } catch (Exception e) {                       // 기타 예외 → 500
            log.error("[REGISTER:AJAX] 예기치 못한 오류", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    /** 로그인 폼 (GET) */
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }
}
