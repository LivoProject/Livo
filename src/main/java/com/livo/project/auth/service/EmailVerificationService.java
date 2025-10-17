// com/livo/project/auth/service/EmailVerificationService.java
package com.livo.project.auth.service;

import com.livo.project.auth.domain.entity.EmailVerification;
import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.EmailVerificationRepository;
import com.livo.project.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository repo;
    private final UserRepository userRepo;

    @Value("${app.mail.from:}")
    private String appMailFrom;

    /* 설정 */
    private static final int CODE_LENGTH = 6;           // 6자리 숫자
    private static final int CODE_TTL_MINUTES = 5;      // 유효 5분
    private static final int RESEND_COOLDOWN_SEC = 60;  // 재전송 쿨다운 60초
    private static final int MAX_ATTEMPTS = 5;          // 최대 시도 횟수

    /* ===== 코드 전송 ===== */
    @Transactional
    public SendResult sendCode(String emailRaw) {
        String email = (emailRaw == null ? "" : emailRaw.trim()).toLowerCase();
        if (email.isBlank()) return SendResult.fail("이메일을 입력해 주세요.");

        // 쿨다운 체크
        Optional<EmailVerification> latestOpt =
                repo.findTopByEmailAndCodeConsumedAtIsNullOrderByCodeExpiresAtDesc(email);
        if (latestOpt.isPresent() && latestOpt.get().getLastSentAt() != null) {
            long sec = Duration.between(latestOpt.get().getLastSentAt(), LocalDateTime.now()).getSeconds();
            long remain = RESEND_COOLDOWN_SEC - sec;
            if (remain > 0) return SendResult.cooldown(remain);
        }

        // 기존 활성 코드 무효화(선택)
        repo.consumeAllActiveCodesByEmail(email);

        // 새 코드 생성/저장
        String code = generateCode();
        String codeHash = sha256(code);

        EmailVerification ev = EmailVerification.builder()
                .email(email)
                .codeHash(codeHash)
                .codeExpiresAt(LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES))
                .attemptCount(0)
                .lastSentAt(LocalDateTime.now())
                .build();
        repo.save(ev);

        // 메일 발송
        SimpleMailMessage msg = new SimpleMailMessage();
        if (appMailFrom != null && !appMailFrom.isBlank()) msg.setFrom(appMailFrom);
        msg.setTo(email);
        msg.setSubject("[Livo] 이메일 확인 코드");
        msg.setText("""
                아래 6자리 코드를 %d분 내에 입력해 주세요.

                인증 코드: %s

                본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.
                """.formatted(CODE_TTL_MINUTES, code));

        try {
            mailSender.send(msg);
            log.info("[MAIL] 코드 발송 성공 → {}", email);
        } catch (MailException e) {
            log.error("[MAIL] 코드 발송 실패", e);
            return SendResult.fail("메일 발송 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        }

        return SendResult.ok("인증 코드를 전송했습니다.");
    }

    /* ===== 코드 검증 ===== */
    @Transactional
    public VerifyResult verifyCode(String emailRaw, String rawCode) {
        String email = (emailRaw == null ? "" : emailRaw.trim()).toLowerCase();
        String code = (rawCode == null ? "" : rawCode.trim());
        if (email.isBlank() || code.isBlank()) {
            return VerifyResult.fail("이메일과 코드를 모두 입력해 주세요.");
        }

        String codeHash = sha256(code);
        Optional<EmailVerification> opt =
                repo.findTopByEmailAndCodeHashAndCodeConsumedAtIsNull(email, codeHash);

        if (opt.isEmpty()) {
            // 불일치 시 가장 최근 미소비 레코드의 시도 횟수만 증가(있으면)
            repo.findTopByEmailAndCodeConsumedAtIsNullOrderByCodeExpiresAtDesc(email)
                    .ifPresent(ev -> repo.incrementAttemptCount(ev.getId()));
            return VerifyResult.fail("코드가 일치하지 않습니다.");
        }

        EmailVerification ev = opt.get();

        // 만료 체크
        if (ev.getCodeExpiresAt() != null && ev.getCodeExpiresAt().isBefore(LocalDateTime.now())) {
            ev.setCodeConsumedAt(LocalDateTime.now());
            repo.save(ev);
            return VerifyResult.fail("코드가 만료되었습니다. 다시 요청해 주세요.");
        }

        // 시도횟수 제한
        if (ev.getAttemptCount() != null && ev.getAttemptCount() >= MAX_ATTEMPTS) {
            ev.setCodeConsumedAt(LocalDateTime.now());
            repo.save(ev);
            return VerifyResult.fail("시도 횟수를 초과했습니다. 코드를 다시 요청해 주세요.");
        }
        // 성공 → 소비
        ev.setCodeConsumedAt(LocalDateTime.now());
        repo.save(ev);

        // 이미 존재하는 사용자면 verified 처리(선택)
        userRepo.findByEmail(email).ifPresent(u -> {
            u.setEmailVerified(true);
            u.setEmailVerifiedAt(LocalDateTime.now());
        });

        return VerifyResult.ok("인증이 완료되었습니다.");
    }

    /* ===== 유틸/설정 ===== */
    private String generateCode() {
        SecureRandom r = new SecureRandom();
        int n = r.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* 결과 DTO */
    public record SendResult(boolean ok, String message, Long cooldownRemainSec) {
        static SendResult ok(String msg) { return new SendResult(true, msg, 0L); }
        static SendResult cooldown(long remain) { return new SendResult(false, "잠시 후 다시 요청해 주세요.", Math.max(remain,1)); }
        static SendResult fail(String msg) { return new SendResult(false, msg, 0L); }
    }
    public record VerifyResult(boolean ok, String message) {
        static VerifyResult ok(String msg) { return new VerifyResult(true, msg); }
        static VerifyResult fail(String msg) { return new VerifyResult(false, msg); }
    }
}
