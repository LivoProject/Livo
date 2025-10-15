package com.livo.project.auth.security;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 해시용 BCrypt
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 활성화 + JS에서 읽을 수 있도록 HttpOnly=false
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

                // ✅ 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // 포워드/에러 디스패처는 모두 허용
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        // 공개 경로
                        .requestMatchers(
                                "/",               // 메인
                                "/main",           // 필요 시 컨트롤러에서 사용
                                "/favicon.ico",
                                "/error",
                                "/auth/login",
                                "/auth/register",
                                "/auth/validate/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**"
                        ).permitAll()
                        // 그 외는 인증 필요
                        .anyRequest().authenticated()
                )

                // ✅ 이전 요청 저장 안 함(불필요한 리다이렉트 방지)
                .requestCache(rc -> rc.requestCache(new NullRequestCache()))

                // ✅ 세션 전략 (단일 블록에서 한 번만 설정)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/") // 세션 만료/잘못된 세션이면 메인으로 보냄
                )

                // ✅ 폼 로그인
                .formLogin(form -> form
                        .loginPage("/auth/login")     // 로그인 페이지 경로 (GET)
                        .loginProcessingUrl("/auth/login") // 로그인 처리 (POST)
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 메인으로
                        .failureUrl("/auth/login?error")
                        .permitAll()
                )

                // ✅ 로그아웃
                .logout(lo -> lo
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
