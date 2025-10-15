// com/livo/project/auth/service/EmailVerificationService.java
package com.livo.project.auth.service;

import com.livo.project.auth.domain.entity.EmailVerification;
import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.EmailVerificationRepository;
import com.livo.project.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository repo;
    private final UserRepository userRepo;

    // 인증 링크에 사용할 호스트 (운영/개발 환경별로 properties에서 주입)
    @Value("${app.host:http://localhost:8080}")
    private String appHost;

    /** 가입 직후: 인증 메일 발송 (기존 토큰 무효화 + 새 토큰 생성) */
    @Transactional
    public void sendVerification(String email) {
        String normalized = (email == null ? "" : email.trim()).toLowerCase();

        // 기존 미소비 토큰 무효화
        repo.consumeAllActiveByEmail(normalized);

        // 원본 토큰 생성 + 해시 저장
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = sha256(rawToken);

        EmailVerification ev = new EmailVerification(
                null,
                normalized,
                tokenHash,
                LocalDateTime.now().plusMinutes(30), // 유효 30분
                null,
                null
        );
        repo.save(ev);

        // 인증 링크
        String link = appHost + "/auth/verify-email?email=" +
                URLEncoder.encode(normalized, StandardCharsets.UTF_8) +
                "&token=" + URLEncoder.encode(rawToken, StandardCharsets.UTF_8);

        // 텍스트 메일 발송 (원하면 HTML 메일로 바꿔줄 수 있음)
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(normalized);
        msg.setSubject("[Livo] 이메일 인증을 완료해주세요");
        msg.setText("""
                아래 링크를 30분 내에 클릭하면 이메일 인증이 완료됩니다.

                %s

                본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.
                """.formatted(link));
        mailSender.send(msg);
    }

    /** 인증 링크 클릭: 토큰 검증 후 사용자 활성화 */
    @Transactional
    public boolean verify(String email, String rawToken) {
        String normalized = (email == null ? "" : email.trim()).toLowerCase();
        String tokenHash = sha256(rawToken);

        Optional<EmailVerification> opt =
                repo.findTopByEmailAndTokenHashAndConsumedAtIsNull(normalized, tokenHash);
        if (opt.isEmpty()) return false;

        EmailVerification ev = opt.get();
        if (ev.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        // 토큰 소비
        ev.setConsumedAt(LocalDateTime.now());
        repo.save(ev);

        // 사용자 활성화
        User user = userRepo.findByEmail(normalized).orElse(null);
        if (user == null) return false;

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        return true;
    }

    /** SHA-256 해시 (원본 토큰은 DB에 저장하지 않음) */
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
}
