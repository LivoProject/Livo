package com.livo.project.auth.service;

import com.livo.project.auth.domain.dto.SignUpRequest;

public interface UserService {
    void register(SignUpRequest req);

    // ▼ 비동기 유효성용 중복 체크
    boolean existsEmail(String email);
    boolean existsNickname(String nickname);
}
