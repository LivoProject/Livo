package com.livo.project.auth.control;

import com.livo.project.auth.domain.dto.SignUpRequest;
import com.livo.project.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
// import org.springframework.http.ResponseEntity; //
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

    //  여기 있던 POST /auth/register 메서드는 삭제 (AuthController가 담당)

    /* 이메일 즉시검증 */
    @GetMapping(value = "/validate/email", produces = "application/json; charset=UTF-8")
    public Map<String, Object> validateEmail(@RequestParam String value, Locale locale) {
        var t = new SignUpRequest();
        t.setEmail(value);
        var binder = new org.springframework.validation.DataBinder(t, "signUpRequest");
        binder.addValidators(validator);
        binder.validate();
        var fe = binder.getBindingResult().getFieldError("email");
        if (fe != null) return Map.of("field","email","valid",false,"message", ms.getMessage(fe, locale));

        String norm = value == null ? "" : value.trim().toLowerCase();
        if (userService.existsEmail(norm))
            return Map.of("field","email","valid",false,"message","이미 사용 중인 이메일입니다.");
        return Map.of("field","email","valid",true);
    }

    /* 닉네임 즉시검증 */
    @GetMapping(value = "/validate/nickname", produces = "application/json; charset=UTF-8")
    public Map<String, Object> validateNickname(@RequestParam String value, Locale locale) {
        var t = new SignUpRequest();
        t.setNickname(value);
        var binder = new org.springframework.validation.DataBinder(t, "signUpRequest");
        binder.addValidators(validator);
        binder.validate();
        var fe = binder.getBindingResult().getFieldError("nickname");
        if (fe != null) return Map.of("field","nickname","valid",false,"message", ms.getMessage(fe, locale));

        String norm = value == null ? "" : value.trim();
        if (userService.existsNickname(norm))
            return Map.of("field","nickname","valid",false,"message","이미 사용 중인 닉네임입니다.");
        return Map.of("field","nickname","valid",true);
    }

    /* 비밀번호 즉시검증 */
    @GetMapping(value = "/validate/password", produces = "application/json; charset=UTF-8")
    public Map<String, Object> validatePassword(@RequestParam String value, Locale locale) {
        var t = new SignUpRequest();
        t.setPassword(value);
        var binder = new org.springframework.validation.DataBinder(t, "signUpRequest");
        binder.addValidators(validator);
        binder.validate();
        var fe = binder.getBindingResult().getFieldError("password");
        if (fe != null) return Map.of("field","password","valid",false,"message", ms.getMessage(fe, locale));
        return Map.of("field","password","valid",true);
    }

    /* 전화번호 즉시검증 */
    @GetMapping(value = "/validate/phone", produces = "application/json; charset=UTF-8")
    public Map<String,Object> validatePhone(@RequestParam String value, Locale locale) {
        var t = new SignUpRequest();
        t.setPhone(value);
        var binder = new org.springframework.validation.DataBinder(t, "signUpRequest");
        binder.addValidators(validator);
        binder.validate();
        var fe = binder.getBindingResult().getFieldError("phone");
        if (fe != null) return Map.of("field","phone","valid",false,"message", ms.getMessage(fe, locale));

        String norm = value == null ? "" : value.replaceAll("[^0-9+]", "").replaceFirst("^\\+82","0");
        if (userService.existsPhone(norm))
            return Map.of("field","phone","valid",false,"message","이미 사용 중인 전화번호입니다.");
        return Map.of("field","phone","valid",true);
    }
}
