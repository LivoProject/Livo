package com.livo.project.auth.control;

import com.livo.project.auth.domain.dto.ApiResult;
import com.livo.project.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/validate")
@RequiredArgsConstructor
public class ValidateController {

    private final UserRepository userRepository;

    // 간단 이메일 형식 체크 (정교함 < 성능/단순성)
    private boolean isEmailFormat(String email) {
        // 아주 가벼운 1차 검사: @ 포함 + 양쪽 문자 존재 + 대충의 길이 제한
        int at = email.indexOf('@');
        return at > 0 && at < email.length() - 1 && email.length() <= 254;
    }

    @GetMapping("/email")
    public ApiResult validateEmail(@RequestParam String email) {
        try {
            if (email == null || email.isBlank()) {
                return new ApiResult(false, "BLANK", "이메일을 입력하세요.");
            }
            String v = email.trim();
            if (!isEmailFormat(v)) {
                return new ApiResult(false, "FORMAT", "올바른 이메일 형식이 아닙니다.");
            }
            if (userRepository.existsByEmail(v)) {
                return new ApiResult(false, "DUPLICATE", "이미 사용 중인 이메일입니다.");
            }
            return new ApiResult(true, "VALID", "사용 가능한 이메일입니다.");
        } catch (Exception e) {
            return new ApiResult(false, "SERVER_ERROR", "서버 오류가 발생했습니다.");
        }
    }

    @GetMapping("/nickname")
    public ApiResult validateNickname(@RequestParam String nickname) {
        try {
            if (nickname == null || nickname.isBlank()) {
                return new ApiResult(false, "BLANK", "닉네임을 입력하세요.");
            }
            String v = nickname.trim();
            if (v.length() < 2 || v.length() > 40) { // DB 길이(40)에 맞춤
                return new ApiResult(false, "FORMAT", "닉네임은 2~40자입니다.");
            }
            if (userRepository.existsByNickname(v)) {
                return new ApiResult(false, "DUPLICATE", "이미 사용 중인 닉네임입니다.");
            }
            return new ApiResult(true, "VALID", "사용 가능한 닉네임입니다.");
        } catch (Exception e) {
            return new ApiResult(false, "SERVER_ERROR", "서버 오류가 발생했습니다.");
        }
    }
}
