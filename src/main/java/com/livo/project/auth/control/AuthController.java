package com.livo.project.auth.control;

import com.livo.project.auth.domain.dto.SignUpRequest; // ← 실제 경로로 맞추세요
import com.livo.project.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
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

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

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

    /** 폼 전송용 (x-www-form-urlencoded) - 화면에서 기본 submit로 보낼 때 사용 */
    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
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
            userService.register(form);
            status.setComplete();
            ra.addFlashAttribute("msg", "회원가입이 완료되었습니다. 로그인 해주세요.");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            log.warn("[REGISTER:FORM] business error: {}", e.getMessage());
            String m = e.getMessage() == null ? "" : e.getMessage();
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
            String root = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : "";
            if (root.contains("uk_user_email"))      binding.rejectValue("email", "Duplicate", "이미 등록된 이메일입니다.");
            else if (root.contains("uq_user_nickname")) binding.rejectValue("nickname", "Duplicate", "이미 사용 중인 닉네임입니다.");
            else if (root.contains("uq_user_phone")) binding.rejectValue("phone", "Duplicate", "이미 등록된 전화번호입니다.");
            else model.addAttribute("error", "이미 사용 중인 정보가 있습니다.");
            return "auth/register";
        } catch (Exception e) {
            log.error("[REGISTER:FORM] unexpected error", e);
            model.addAttribute("error", "처리 중 오류가 발생했습니다.");
            return "auth/register";
        }
    }

    /** ✅ AJAX(JSON) 회원가입 */
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> registerJson(@Valid @RequestBody SignUpRequest form) {
        log.info("[REGISTER:AJAX] email={}, nickname={}", form.getEmail(), form.getNickname());

        try {
            userService.register(form); // 내부에서 bcrypt 인코딩, DB 저장
            return ResponseEntity.ok(Map.of("success", true));

        } catch (IllegalArgumentException e) {
            // "field:message" 형식 예외 -> 필드 에러 JSON
            String msg = e.getMessage() == null ? "" : e.getMessage();
            int i = msg.indexOf(':');
            if (i > 0) {
                String field = msg.substring(0, i).trim();
                String text  = msg.substring(i + 1).trim();
                return ResponseEntity.badRequest().body(Map.of(field, text));
            }
            return ResponseEntity.internalServerError().body(Map.of("error", "처리 중 오류가 발생했습니다."));

        } catch (DataIntegrityViolationException e) {
            String root = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : "";
            Map<String, String> err = new HashMap<>();
            if (root.contains("uk_user_email"))        err.put("email", "이미 등록된 이메일입니다.");
            else if (root.contains("uq_user_nickname")) err.put("nickname", "이미 사용 중인 닉네임입니다.");
            else if (root.contains("uq_user_phone"))    err.put("phone", "이미 등록된 전화번호입니다.");
            else err.put("error", "이미 사용 중인 정보가 있습니다.");
            return ResponseEntity.badRequest().body(err);

        } catch (Exception e) {
            log.error("[REGISTER:AJAX] unexpected error", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    /** Bean Validation 실패 → JSON으로 응답 (AJAX 경로에만 적용) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    /** 로그인 폼 (GET) */
    @GetMapping("/login")
    public String loginForm() { return "auth/login"; }
}
