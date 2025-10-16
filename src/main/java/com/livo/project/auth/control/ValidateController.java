// src/main/java/com/livo/project/auth/control/ValidateController.java
package com.livo.project.auth.control;

import com.livo.project.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/validate")
@RequiredArgsConstructor
public class ValidateController {

    private final UserRepository userRepository;

    // ───────────────── helpers ─────────────────
    // 가벼운 이메일 형식 체크 (@ 포함, 앞/뒤에 문자가 있고 전체 길이 254 이하)
    private boolean isEmailFormat(String email) {
        if (email == null) return false;
        String v = email.trim();
        int at = v.indexOf('@');
        return !v.isBlank() && at > 0 && at < v.length() - 1 && v.length() <= 254;
    }

    // ───────────────── API: 이메일 검증 ─────────────────
    // JS가 ?value=... 로 보내도 되고, ?email=... 로 보내도 됨 (둘 다 수용)
    @GetMapping("/email")
    public Map<String, Object> validateEmail(
            @RequestParam(required = false, name = "email") String emailParam,
            @RequestParam(required = false, name = "value") String valueParam
    ) {
        String email = (emailParam != null ? emailParam : valueParam);
        if (email == null || email.isBlank()) {
            return Map.of("valid", false, "message", "이메일을 입력하세요.");
        }
        String v = email.trim().toLowerCase();
        if (!isEmailFormat(v)) {
            return Map.of("valid", false, "message", "올바른 이메일 형식이 아닙니다.");
        }
        if (userRepository.existsByEmail(v)) {
            return Map.of("valid", false, "message", "이미 사용 중인 이메일입니다.");
        }
        return Map.of("valid", true, "message", "사용 가능한 이메일입니다.");
    }

    // ───────────────── API: 닉네임 검증 ─────────────────
    @GetMapping("/nickname")
    public Map<String, Object> validateNickname(
            @RequestParam(required = false, name = "nickname") String nicknameParam,
            @RequestParam(required = false, name = "value") String valueParam
    ) {
        String nickname = (nicknameParam != null ? nicknameParam : valueParam);
        if (nickname == null || nickname.isBlank()) {
            return Map.of("valid", false, "message", "닉네임을 입력하세요.");
        }
        String v = nickname.trim();
        if (v.length() < 2 || v.length() > 40) { // DB 제약에 맞춰 2~40자
            return Map.of("valid", false, "message", "닉네임은 2~40자입니다.");
        }
        if (userRepository.existsByNickname(v)) {
            return Map.of("valid", false, "message", "이미 사용 중인 닉네임입니다.");
        }
        return Map.of("valid", true, "message", "사용 가능한 닉네임입니다.");
    }

    // ───────────────── API: 비밀번호 검증 ─────────────────
    // JS가 ?value=... 또는 ?password=...로 호출해도 OK
    @GetMapping("/password")
    public Map<String, Object> validatePassword(
            @RequestParam(required = false, name = "password") String pwParam,
            @RequestParam(required = false, name = "value") String valueParam
    ) {
        String pw = (pwParam != null ? pwParam : valueParam);
        if (pw == null || pw.isBlank()) {
            return Map.of("valid", false, "message", "비밀번호를 입력하세요.");
        }
        String v = pw.trim();

        // 예시 정책: 영문/숫자/특수문자 포함, 8~20자
        boolean lenOk   = v.length() >= 8 && v.length() <= 20;
        boolean hasNum  = v.chars().anyMatch(Character::isDigit);
        boolean hasAlpha= v.chars().anyMatch(Character::isLetter);
        boolean hasSpec = v.matches(".*[^a-zA-Z0-9].*");

        if (!(lenOk && hasNum && hasAlpha && hasSpec)) {
            return Map.of("valid", false, "message", "영문·숫자·특수문자 포함 8~20자로 입력하세요.");
        }
        return Map.of("valid", true, "message", "사용 가능한 비밀번호입니다.");
    }
}
