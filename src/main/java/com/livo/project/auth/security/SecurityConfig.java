package com.livo.project.auth.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /** ✅ 당신의 구현체로 바꿔주세요: com.livo.project.auth.security.CustomUserDetailsService */
    private final UserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** ✅ DaoAuthenticationProvider 등록: isEnabled()가 emailVerified와 연결되어 있어야 함 */
    @Bean
    public DaoAuthenticationProvider daoAuthProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(customUserDetailsService);
        p.setPasswordEncoder(encoder);
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider daoAuthProvider) throws Exception {
        http
                // ✅ CSRF: JS에서 읽을 수 있도록 HttpOnly=false (X-CSRF-TOKEN 사용)
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

                // ✅ 인증/인가
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers(
                                "/", "/main", "/favicon.ico", "/error",
                                "/auth/login",
                                "/auth/register",            // 폼/JSON 모두 동일 경로
                                "/auth/verify-email",        // ✅ 이메일 인증 콜백 허용
                                "/auth/validate/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ 우리가 등록한 DaoAuthenticationProvider 사용
                .authenticationProvider(daoAuthProvider)

                // ✅ 이전 요청 저장 안 함
                .requestCache(rc -> rc.requestCache(new NullRequestCache()))

                // ✅ 세션 전략
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/")
                )

                // ✅ 폼 로그인
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
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

        /* (선택) Remember-me가 필요하면 주석 해제
        http.rememberMe(rm -> rm
            .userDetailsService(customUserDetailsService)
            .tokenValiditySeconds(60 * 60 * 24 * 14) // 14일
            .rememberMeParameter("remember-me")
        );
        */

        return http.build();
    }
}
