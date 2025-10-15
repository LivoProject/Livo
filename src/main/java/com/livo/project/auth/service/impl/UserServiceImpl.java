package com.livo.project.auth.service.impl;

import com.livo.project.auth.domain.dto.SignUpRequest; // ✅ 이걸로 통일!
import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import com.livo.project.auth.service.BusinessException;
import com.livo.project.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Override
    public boolean existsEmail(String email) {
        if (email == null) return false;
        return users.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public boolean existsNickname(String nickname) {
        if (nickname == null) return false;
        return users.existsByNickname(nickname.trim());
    }

    @Transactional
    @Override
    public void register(SignUpRequest req) { // ✅ 인터페이스와 동일한 타입/이름
        final String email = safe(req.getEmail()).toLowerCase();
        final String nickname = safe(req.getNickname());
        final String name = safe(req.getName());
        final String phone = normalizePhone(req.getPhone());

        if (users.existsByEmail(email))
            throw new BusinessException("email", "이미 사용 중인 이메일입니다.");
        if (users.existsByNickname(nickname))
            throw new BusinessException("nickname", "이미 사용 중인 닉네임입니다.");
        if (phone != null && users.existsByPhone(phone))
            throw new BusinessException("phone", "이미 사용 중인 전화번호입니다.");

        User u = new User();
        u.setEmail(email);
        u.setPassword(encoder.encode(req.getPassword()));
        u.setName(name);
        u.setNickname(nickname);
        u.setPhone(phone);
        u.setStatus(true);              // ⚠️ 엔티티가 boolean이면 OK, int면 setStatus(1)로
        u.setRoleId(1);                 // ⚠️ 엔티티에 roleId 필드가 실제로 있어야 함
        u.setCreatedAt(LocalDateTime.now()); // ⚠️ DB 컬럼(createdAt) 존재해야 함

        if (req.getBirth() != null && !req.getBirth().isBlank()) {
            u.setBirth(LocalDate.parse(req.getBirth())); // 입력 포맷: yyyy-MM-dd 가정
        }
        if ("M".equalsIgnoreCase(req.getGender())) u.setGender(User.Gender.M);
        else if ("F".equalsIgnoreCase(req.getGender())) u.setGender(User.Gender.F);

        users.saveAndFlush(u);
        log.info("[REGISTER] saved user email={}", u.getEmail());
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private String normalizePhone(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String digits = raw.replaceAll("[^0-9+]", "");
        if (digits.isBlank()) return null;
        if (digits.startsWith("+")) return digits;
        if (digits.startsWith("0")) return "+82" + digits.substring(1);
        return digits;
    }
}
