package com.livo.project.auth.service.impl;

import com.livo.project.auth.domain.dto.SignUpRequest;
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
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    // ========== 비동기 유효성 ==========
    @Override
    @Transactional(readOnly = true)
    public boolean existsEmail(String email) {
        if (email == null) return false;
        return users.existsByEmail(email.trim().toLowerCase(Locale.ROOT));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsNickname(String nickname) {
        if (nickname == null) return false;
        return users.existsByNickname(nickname.trim());
    }

    // ========== 회원가입 ==========
    @Override
    @Transactional
    public void register(SignUpRequest req) {
        final String email    = safe(req.getEmail()).toLowerCase(Locale.ROOT);
        final String nickname = safe(req.getNickname());
        final String name     = safe(req.getName());
        final String phone    = normalizePhone(req.getPhone());

        // 사전 중복 체크 (최종 방어는 DB UNIQUE)
        if (users.existsByEmail(email))      throw new BusinessException("email", "이미 사용 중인 이메일입니다.");
        if (users.existsByNickname(nickname))throw new BusinessException("nickname", "이미 사용 중인 닉네임입니다.");
        if (phone != null && hasExistsByPhone() && users.existsByPhone(phone))
            throw new BusinessException("phone", "이미 사용 중인 전화번호입니다.");

        User u = new User();
        u.setEmail(email);
        u.setPassword(encoder.encode(req.getPassword()));
        u.setName(name);
        u.setNickname(nickname);
        u.setPhone(phone);
        u.setStatus(true);
        u.setRoleId(1);
        u.setEmailVerified(false); // 가입 시 미인증

        // birth / gender 매핑 (SignUpRequest가 String인 경우 ISO yyyy-MM-dd 가정)
        if (req.getBirth() != null && !req.getBirth().isBlank()) {
            u.setBirth(LocalDate.parse(req.getBirth()));
        }
        if ("M".equalsIgnoreCase(req.getGender()))      u.setGender(User.Gender.M);
        else if ("F".equalsIgnoreCase(req.getGender())) u.setGender(User.Gender.F);

        users.save(u); // saveAndFlush 불필요 (트랜잭션 종료 시 플러시)
        log.info("[REGISTER] saved user email={}", u.getEmail());
    }

    // ========== 마이페이지: 닉네임 변경 ==========
    @Override
    @Transactional
    public void updateNickname(Long userId, String nickname) {
        String v = safe(nickname);
        if (v.length() < 2 || v.length() > 40)
            throw new BusinessException("nickname", "닉네임은 2~40자입니다.");
        if (users.existsByNickname(v))
            throw new BusinessException("nickname", "이미 사용 중인 닉네임입니다.");

        User u = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        u.setNickname(v);
        // updatedAt은 @UpdateTimestamp가 알아서
    }

    // ========== 마이페이지: 비밀번호 변경 ==========
    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User u = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (!encoder.matches(currentPassword, u.getPassword()))
            throw new BusinessException("currentPassword", "현재 비밀번호가 일치하지 않습니다.");
        u.setPassword(encoder.encode(newPassword));
        // 필요시 u.setLastPasswordChangedAt(LocalDateTime.now());
    }

    // ========== 조회 ==========
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return users.findByEmail(email.trim().toLowerCase(Locale.ROOT));
    }

    // ========== 헬퍼 ==========
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private String normalizePhone(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String digits = raw.replaceAll("[^0-9+]", "");
        return digits.isBlank() ? null : digits;
    }

    /** 레포지토리에 existsByPhone(String) 메소드가 존재하는지 확인 (없으면 호출 안 함) */
    private boolean hasExistsByPhone() {
        try {
            UserRepository.class.getMethod("existsByPhone", String.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
