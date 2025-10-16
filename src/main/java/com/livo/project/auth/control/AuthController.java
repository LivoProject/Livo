// com/livo/project/auth/control/AuthController.java
package com.livo.project.auth.control;

import com.livo.project.auth.domain.dto.SignUpRequest;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
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

    /** ✅ 모든 String 필드의 앞뒤 공백 제거 + 빈 문자열을 null로 변환 */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /** ✅ 회원가입 폼 페이지 (GET)
     *  - 세션에 이메일 인증이 완료된 사용자는 이메일 입력칸을 잠금(readonly)
     */
    @GetMapping("/register")
    public String registerForm(@ModelAttribute("signUpRequest") SignUpRequest form,
                               @SessionAttribute(value = "VERIFIED_EMAIL", required = false) String verifiedEmail,
                               Model model) {
        log.info("[REGISTER:GET] 회원가입 폼 진입 - VERIFIED_EMAIL={}", verifiedEmail);
        if (verifiedEmail != null) {
            model.addAttribute("verifiedEmail", verifiedEmail); // JSP 렌더링용
        }
        return "auth/register";
    }

    // =====================================================
    // ✅ 1. 이메일 인증 코드 관련 API (AJAX)
    // =====================================================

    /** ✅ 인증 코드 전송
     *  - 입력된 이메일로 인증 코드를 전송 (쿨다운/횟수 제한 포함)
     *  - EmailVerificationService에서 비즈니스 로직 처리
     */
    @PostMapping(value = "/send-code", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
        // 쿨다운 중 or 횟수 초과 시 429 (Too Many Requests)
        return ResponseEntity.status(429).body(Map.of(
                "success", false,
                "message", r.message(),
                "cooldownRemainSec", r.cooldownRemainSec()
        ));
    }

    /** ✅ 인증 코드 검증
     *  - 사용자가 입력한 코드와 DB에 저장된 해시 비교
     *  - 성공 시 세션에 VERIFIED_EMAIL 저장 → 이후 회원가입 허용
     */
    @PostMapping(value = "/verify-code", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> verifyCode(@RequestParam("email") String email,
                                        @RequestParam("code") String code,
                                        HttpSession session) {
        log.info("[VERIFY-CODE] email={}, code=******", email);
        var r = emailVerificationService.verifyCode(email, code);

        if (r.ok()) {
            session.setAttribute("VERIFIED_EMAIL", email.toLowerCase());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", r.message()
            ));
        }

        // 잘못된 코드, 만료 등 일반 실패는 400
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", r.message()
        ));
    }

    // 🔸 기존 링크 방식 (verify-email) 은 사용하지 않으므로 제거/주석 유지
    // @GetMapping("/verify-email")
    // public String verifyEmail(...) { ... }

    // =====================================================
    // ✅ 2. 회원가입 처리 (폼 or JSON)
    // =====================================================

    /** ✅ 회원가입 폼 전송 (기본 POST)
     *  - 세션에 VERIFIED_EMAIL이 있어야만 가입 가능
     *  - 가입 완료 후 세션에서 VERIFIED_EMAIL 제거
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registerFormPost(@Valid @ModelAttribute("signUpRequest") SignUpRequest form,
                                   BindingResult binding,
                                   Model model,
                                   RedirectAttributes ra,
                                   @SessionAttribute(value = "VERIFIED_EMAIL", required = false) String verifiedEmail,
                                   HttpSession session) {

        log.info("[REGISTER:FORM] 회원가입 요청 email={}, nickname={}, phone={}", form.getEmail(), form.getNickname(), form.getPhone());
        log.info("[REGISTER:FORM] 세션 VERIFIED_EMAIL={}", verifiedEmail);

        // 이메일 미인증 차단
        if (verifiedEmail == null || !verifiedEmail.equalsIgnoreCase(form.getEmail())) {
            binding.rejectValue("email", "email.notVerified", "이메일 인증 후 진행해 주세요.");
            return "auth/register";
        }

        // Bean Validation 실패 (입력값 형식 오류)
        if (binding.hasErrors()) {
            log.warn("[REGISTER:FORM] BeanValidation 실패 => {}", binding.getAllErrors());
            return "auth/register";
        }

        try {
            // 사용자 등록
            userService.register(form);
            // 세션 정리
            session.removeAttribute("VERIFIED_EMAIL");
            // 성공 메시지
            ra.addFlashAttribute("msg", "회원가입이 완료되었습니다. 로그인해 주세요.");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            // 비즈니스 예외 처리 (예: 비밀번호 정책 불만족 등)
            log.warn("[REGISTER:FORM] 비즈니스 예외: {}", e.getMessage());
            String m = Optional.ofNullable(e.getMessage()).orElse("");
            int i = m.indexOf(':');
            if (i > 0) {
                String field = m.substring(0, i).trim();
                String text = m.substring(i + 1).trim();
                binding.rejectValue(field, "Business", text);
            } else {
                model.addAttribute("error", m.isBlank() ? "처리 중 오류가 발생했습니다." : m);
            }
            return "auth/register";

        } catch (DataIntegrityViolationException e) {
            // DB 제약조건 위반 (중복 이메일, 닉네임 등)
            log.warn("[REGISTER:FORM] 제약조건 위반: {}", e.getMessage());
            String root = Optional.ofNullable(e.getMostSpecificCause()).map(Throwable::getMessage).orElse("");
            if (root.contains("uk_user_email")) binding.rejectValue("email", "Duplicate", "이미 등록된 이메일입니다.");
            else if (root.contains("uq_user_nickname")) binding.rejectValue("nickname", "Duplicate", "이미 사용 중인 닉네임입니다.");
            else if (root.contains("uq_user_phone")) binding.rejectValue("phone", "Duplicate", "이미 등록된 전화번호입니다.");
            else model.addAttribute("error", "이미 사용 중인 정보가 있습니다.");
            return "auth/register";

        } catch (Exception e) {
            // 그 외 예기치 못한 오류
            log.error("[REGISTER:FORM] 예기치 못한 오류", e);
            model.addAttribute("error", "처리 중 오류가 발생했습니다.");
            return "auth/register";
        }
    }

    /** ✅ AJAX(JSON) 회원가입
     *  - 프론트엔드에서 fetch(JSON)으로 호출 시 이 메서드가 실행됨
     *  - 성공 시 {"success": true} JSON 반환
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
            userService.register(form);
            session.removeAttribute("VERIFIED_EMAIL");
            return ResponseEntity.ok(Map.of("success", true));

        } catch (IllegalArgumentException e) {
            // 비즈니스 예외 (필드 지정된 메시지 형태)
            String msg = Optional.ofNullable(e.getMessage()).orElse("");
            int i = msg.indexOf(':');
            if (i > 0) {
                String field = msg.substring(0, i).trim();
                String text  = msg.substring(i + 1).trim();
                return ResponseEntity.badRequest().body(Map.of(field, text));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "요청을 처리할 수 없습니다."));

        } catch (DataIntegrityViolationException e) {
            // 중복값 예외 처리
            String root = Optional.ofNullable(e.getMostSpecificCause()).map(Throwable::getMessage).orElse("");
            Map<String, String> err = new HashMap<>();
            if (root.contains("uk_user_email")) err.put("email", "이미 등록된 이메일입니다.");
            else if (root.contains("uq_user_nickname")) err.put("nickname", "이미 사용 중인 닉네임입니다.");
            else if (root.contains("uq_user_phone")) err.put("phone", "이미 등록된 전화번호입니다.");
            else err.put("error", "이미 사용 중인 정보가 있습니다.");
            return ResponseEntity.badRequest().body(err);

        } catch (Exception e) {
            // 기타 서버 오류
            log.error("[REGISTER:AJAX] 예기치 못한 오류", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    /** ✅ Bean Validation 예외 처리
     *  - @Valid 실패 시 발생하는 예외를 JSON 형태로 반환 (AJAX 전용)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    /** ✅ 로그인 폼 (GET)
     *  - 단순히 JSP 렌더링만 수행
     */
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }
}
