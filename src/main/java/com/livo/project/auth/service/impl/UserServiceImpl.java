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

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

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
        return users.existsByEmailIgnoreCaseAndProvider(
                email.trim().toLowerCase(Locale.ROOT),
                "LOCAL"
        );
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsNickname(String nickname) {
        if (nickname == null) return false;
        return users.existsByNickname(nickname.trim());
    }

    @Override @Transactional(readOnly = true)
    public boolean existsPhone(String phoneRaw) {
        String phone = normalizePhone(phoneRaw);
        return phone != null && users.existsByPhone(phone);
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
        if (users.existsByEmailIgnoreCaseAndProvider(email, "LOCAL"))
            throw new BusinessException("email", "이미 사용 중인 이메일입니다.");
        if (users.existsByNickname(nickname))
            throw new BusinessException("nickname", "이미 사용 중인 닉네임입니다.");
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

        u.setProvider("LOCAL");
        u.setProviderId(UUID.randomUUID().toString());

        u.setEmailVerified(true);
        u.setEmailVerifiedAt(java.time.LocalDateTime.now());

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
        return users.findByEmailIgnoreCaseAndProvider(email.trim().toLowerCase(Locale.ROOT), "LOCAL");
    }


    // ========== 소셜 계정 연동 ==========
    @Override
    @Transactional
    public void linkSocialAccount(String email, String provider) {
        final String emailNorm = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);

        // 1) 로컬 계정은 provider=LOCAL로 명확히 조회
        User localUser = users.findByEmailIgnoreCaseAndProvider(emailNorm, "LOCAL")
                .orElseThrow(() -> new BusinessException("user", "로컬 계정을 찾을 수 없습니다."));

        // 2) (중요) 소셜 로그인 과정에서 받은 providerId가 반드시 있어야 함
        //    - 실제에선 SecurityContext 또는 파라미터로 받아오세요.
        String providerId = obtainProviderIdFromContext(); // TODO 구현

        // 3) 동일 소셜 식별자 존재하면 그걸 '연결'했다고 보고 끝
        Optional<User> socialOpt = users.findByProviderAndProviderId(provider, providerId);
        if (socialOpt.isPresent()) {
            // 필요시 연결 테이블 등록만 하고 return
            linkUsers(localUser.getId(), socialOpt.get().getId(), provider, providerId); // 선택
            log.info("[LINK] 이미 존재하는 소셜 계정과 연결 - local={}, social={}", localUser.getId(), socialOpt.get().getId());
            return;
        }

        // 4) 소셜 신규 레코드 생성 (이메일 같아도 별도 계정)
        User social = new User();
        social.setProvider(provider);              // GOOGLE / KAKAO / NAVER ...
        social.setProviderId(providerId);          // sub / id 등
        social.setEmail(emailNorm);                // 제공되면 저장 (없어도 무방)
        social.setName(localUser.getName());       // 혹은 소셜 프로필명
        social.setNickname(UUID.randomUUID().toString()); // 닉네임 정책에 맞게
        social.setStatus(true);
        social.setRoleId(localUser.getRoleId());   // 정책에 맞게
        social.setEmailVerified(true);
        social.setEmailVerifiedAt(LocalDateTime.now());
        users.save(social);

        // 5) (선택) 연결 테이블에 link 기록
        linkUsers(localUser.getId(), social.getId(), provider, providerId);

        log.info("[LINK] 로컬-소셜 계정 분리 생성 & 연결 - local={}, social={}, provider={}",
                localUser.getId(), social.getId(), provider);
    }

    // 예시: 실제 구현은 별도 Repository/Entity 로 만드세요.
    private void linkUsers(Long localUserId, Long socialUserId, String provider, String providerId) {
        // user_link 테이블에 INSERT 등
    }

    private String obtainProviderIdFromContext() {
        // OAuth2 로그인 흐름에서 받은 sub(id)를 꺼내는 코드로 교체
        throw new UnsupportedOperationException("providerId 주입 로직을 구현하세요.");
    }
    // ========== 헬퍼 ==========
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private String normalizePhone(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String s = raw.replaceAll("[^0-9+]", ""); // 숫자/+만
        if (s.startsWith("+82")) s = "0" + s.substring(3); // 국제코드 -> 국내표기
        s = s.replaceAll("\\D", ""); // 최종적으로 숫자만
        return s.isBlank() ? null : s;
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
