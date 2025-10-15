package com.livo.project.auth.control;

import com.livo.project.auth.domain.dto.SignUpRequest;
import com.livo.project.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    private final SmartValidator validator;
    private final MessageSource ms;
    private final UserService userService;

    public AuthRestController(SmartValidator validator, MessageSource ms, UserService userService) {
        this.validator = validator;
        this.ms = ms;
        this.userService = userService;
    }

    /* =========================
       회원가입(비동기 JSON 제출)
       - 항상 JSON으로 응답(성공/실패 모두)
       - 성공 시 redirect URL도 함께 반환
       ========================= */
    @PostMapping(
            value = "/register",
            consumes = "application/json",
            produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<?> register(@RequestBody @Valid SignUpRequest req) {
        try {
            userService.register(req);
            return ResponseEntity
                    .status(201)
                    .body(Map.of("ok", true, "redirect", "/auth/login?registered"));
        } catch (IllegalArgumentException e) {
            // 서비스에서 "field:message" 형식으로 던진 경우를 JSON으로 매핑
            String m = e.getMessage() == null ? "" : e.getMessage();
            int i = m.indexOf(':');
            if (i > 0) {
                String field = m.substring(0, i);
                String text  = m.substring(i + 1);
                return ResponseEntity.badRequest().body(Map.of(
                        "ok", false,
                        field, text
                ));
            }
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "error", m.isBlank() ? "처리 중 오류가 발생했습니다." : m
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "ok", false,
                    "error", "처리 중 오류가 발생했습니다."
            ));
        }
    }

    /* =========================
       이메일 즉시검증
       - 형식 검사(Bean Validation)
       - 중복 검사(서비스와 동일 기준: trim + toLowerCase)
       응답: { field:"email", valid:boolean, message?:string }
       ========================= */
    @GetMapping(value = "/validate/email", produces = "application/json; charset=UTF-8")
    public Map<String, Object> validateEmail(@RequestParam String value, Locale locale) {
        var t = new SignUpRequest();
        t.setEmail(value);

        var binder = new org.springframework.validation.DataBinder(t, "signUpRequest");
        binder.addValidators(validator);
        binder.validate();

        var fe = binder.getBindingResult().getFieldError("email");
        if (fe != null) {
            return Map.of("field", "email", "valid", false, "message", ms.getMessage(fe, locale));
        }

        String norm = value == null ? "" : value.trim().toLowerCase();
        if (userService.existsEmail(norm)) {
            return Map.of("field", "email", "valid", false, "message", "이미 사용 중인 이메일입니다.");
        }
        return Map.of("field", "email", "valid", true);
    }

    /* =========================
       닉네임 즉시검증
       - 형식 검사(Bean Validation)
       - 중복 검사(서비스와 동일 기준: trim)
       응답: { field:"nickname", valid:boolean, message?:string }
       ========================= */
    @GetMapping(value = "/validate/nickname", produces = "application/json; charset=UTF-8")
    public Map<String, Object> validateNickname(@RequestParam String value, Locale locale) {
        var t = new SignUpRequest();
        t.setNickname(value);

        var binder = new org.springframework.validation.DataBinder(t, "signUpRequest");
        binder.addValidators(validator);
        binder.validate();

        var fe = binder.getBindingResult().getFieldError("nickname");
        if (fe != null) {
            return Map.of("field", "nickname", "valid", false, "message", ms.getMessage(fe, locale));
        }

        String norm = value == null ? "" : value.trim();
        if (userService.existsNickname(norm)) {
            return Map.of("field", "nickname", "valid", false, "message", "이미 사용 중인 닉네임입니다.");
        }
        return Map.of("field", "nickname", "valid", true);
    }

    /* =========================
       비밀번호 즉시검증(형식만)
       - Bean Validation으로 정책 검사
       응답: { field:"password", valid:boolean, message?:string }
       ========================= */
    @GetMapping(value = "/validate/password", produces = "application/json; charset=UTF-8")
    public Map<String, Object> validatePassword(@RequestParam String value, Locale locale) {
        var t = new SignUpRequest();
        t.setPassword(value);

        var binder = new org.springframework.validation.DataBinder(t, "signUpRequest");
        binder.addValidators(validator);
        binder.validate();

        var fe = binder.getBindingResult().getFieldError("password");
        if (fe != null) {
            return Map.of("field", "password", "valid", false, "message", ms.getMessage(fe, locale));
        }
        return Map.of("field", "password", "valid", true);
    }
}
