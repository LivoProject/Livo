package com.livo.project.auth.control;

import com.livo.project.auth.domain.dto.SignUpRequest;
import com.livo.project.auth.service.EmailVerificationService;
import com.livo.project.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /** 모든 String 입력값 트리밍 + 빈문자열 -> null */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /** 회원가입 폼 (GET) */
    @GetMapping("/register")
    public String registerForm(@ModelAttribute("signUpRequest") SignUpRequest form) {
        return "auth/register"; // /WEB-INF/views/auth/register.jsp
    }

    /** ✅ 폼 전송 (x-www-form-urlencoded) */
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String registerFormPost(@Valid @ModelAttribute("signUpRequest") SignUpRequest form,
                                   BindingResult binding,
                                   Model model,
                                   RedirectAttributes ra,
                                   SessionStatus status) {

        log.info("[REGISTER:FORM] email={}, name={}, nickname={}, phone={}",
                form.getEmail(), form.getName(), form.getNickname(), form.getPhone());

        if (binding.hasErrors()) {
            log.warn("[REGISTER:FORM] BeanValidation errors => {}", binding.getAllErrors());
            return "auth/register";
        }

        try {
            // 회원 생성(중복검사 + 암호화 저장)
            userService.register(form);

            // 이메일 인증 메일 발송
            emailVerificationService.sendVerification(form.getEmail());

            status.setComplete();
            ra.addFlashAttribute("msg", "회원가입이 완료되었습니다. 이메일 인증 후 로그인해주세요.");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            log.warn("[REGISTER:FORM] business error: {}", e.getMessage());
            String m = Optional.ofNullable(e.getMessage()).orElse("");
            int i = m.indexOf(':');
            if (i > 0) {
                String field = m.substring(0, i).trim();
                String text  = m.substring(i + 1).trim();
                binding.rejectValue(field, "Business", text);
            } else {
                model.addAttribute("error", m.isBlank() ? "처리 중 오류가 발생했습니다." : m);
            }
            return "auth/register";

        } catch (DataIntegrityViolationException e) {
            log.warn("[REGISTER:FORM] data integrity violation", e);
            String root = Optional.ofNullable(e.getMostSpecificCause())
                    .map(Throwable::getMessage).orElse("");
            if (root.contains("uk_user_email"))          binding.rejectValue("email", "Duplicate", "이미 등록된 이메일입니다.");
            else if (root.contains("uq_user_nickname"))  binding.rejectValue("nickname", "Duplicate", "이미 사용 중인 닉네임입니다.");
            else if (root.contains("uq_user_phone"))     binding.rejectValue("phone", "Duplicate", "이미 등록된 전화번호입니다.");
            else                                         model.addAttribute("error", "이미 사용 중인 정보가 있습니다.");
            return "auth/register";

        } catch (Exception e) {
            log.error("[REGISTER:FORM] unexpected error", e);
            model.addAttribute("error", "처리 중 오류가 발생했습니다.");
            return "auth/register";
        }
    }

    /** ✅ AJAX(JSON) 회원가입 */
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> registerJson(@Valid @RequestBody SignUpRequest form) {
        log.info("[REGISTER:AJAX] email={}, nickname={}", form.getEmail(), form.getNickname());
        try {
            // 1. 회원 등록
            userService.register(form);
            // 2. 인증 메일 발송
            emailVerificationService.sendVerification(form.getEmail());
            return ResponseEntity.ok(Map.of("success", true));

        } catch (IllegalArgumentException e) {
            String msg = Optional.ofNullable(e.getMessage()).orElse("");
            int i = msg.indexOf(':');
            if (i > 0) {
                String field = msg.substring(0, i).trim();
                String text  = msg.substring(i + 1).trim();
                return ResponseEntity.badRequest().body(Map.of(field, text));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "요청을 처리할 수 없습니다."));

        } catch (DataIntegrityViolationException e) {
            String root = Optional.ofNullable(e.getMostSpecificCause())
                    .map(Throwable::getMessage).orElse("");
            Map<String, String> err = new HashMap<>();
            if (root.contains("uk_user_email"))          err.put("email", "이미 등록된 이메일입니다.");
            else if (root.contains("uq_user_nickname"))  err.put("nickname", "이미 사용 중인 닉네임입니다.");
            else if (root.contains("uq_user_phone"))     err.put("phone", "이미 등록된 전화번호입니다.");
            else                                         err.put("error", "이미 사용 중인 정보가 있습니다.");
            return ResponseEntity.badRequest().body(err);

        } catch (Exception e) {
            log.error("[REGISTER:AJAX] unexpected error", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    /** ✅ 이메일 인증 확인 */
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String email,
                              @RequestParam String token,
                              Model model) {
        boolean verified = emailVerificationService.verify(email, token);
        model.addAttribute("verified", verified);
        return "auth/verify_result"; // /WEB-INF/views/auth/verify_result.jsp
    }

    /** ✅ Bean Validation 실패 → JSON으로 응답 (AJAX 전용) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    /** ✅ 로그인 폼 (GET) */
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }
}
