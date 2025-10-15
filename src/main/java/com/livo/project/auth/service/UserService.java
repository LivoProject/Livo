package com.livo.project.auth.service;

import com.livo.project.auth.domain.dto.SignUpRequest;
import com.livo.project.auth.domain.entity.User;   // ✅ 엔티티 import
import java.util.Optional;                         // ✅ Optional import

public interface UserService {

    // 회원가입
    void register(SignUpRequest req);

    // 마이페이지
    void updateNickname(Long userId, String nickname);
    void changePassword(Long userId, String currentPassword, String newPassword);

    // 조회
    Optional<User> findByEmail(String email);      // ✅ 세미콜론 추가

    // ▼ 비동기 유효성용 중복 체크
    boolean existsEmail(String email);
    boolean existsNickname(String nickname);
}
