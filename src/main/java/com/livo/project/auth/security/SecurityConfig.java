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

    /**  사용자 인증 로직 구현체 (CustomUserDetailsService 사용) */
    private final UserDetailsService customUserDetailsService;

    /**  비밀번호 암호화용 BCrypt 인코더 (DB의 해시와 동일한 알고리즘 사용) */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**  AuthenticationProvider 설정
     * - Spring Security가 사용자 정보를 로드할 때 사용할 Provider
     * - UserDetailsService + PasswordEncoder 연결
     * - User 엔티티의 emailVerified 필드를 isEnabled()에 매핑하면
     *   이메일 미인증 계정은 로그인 불가로 자동 차단 가능
     */
    @Bean
    public DaoAuthenticationProvider daoAuthProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    /**  보안 필터 체인 설정 */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider daoAuthProvider) throws Exception {

        http
                /* -------------------------------
                 * [1] CSRF 설정
                 * -------------------------------
                 * - 기본적으로 CSRF 보호를 유지
                 * - JS에서 CSRF 토큰을 읽어 Ajax 요청에 포함할 수 있도록
                 *   HttpOnly=false 설정 (쿠키에 저장되지만 JS 접근 가능)
                 */
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/lecture/like/**") // <-민영추가!!
                )


                /* -------------------------------
                 * [2] 요청 인가(Authorization) 규칙 설정
                 * -------------------------------
                 * - DispatcherType.FORWARD/ERROR 는 항상 허용 (내부 요청)
                 * - 아래 경로들은 인증 없이 접근 가능 (permitAll)
                 *   -> 회원가입, 로그인, 이메일 코드 인증, 정적 리소스 등
                 */
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers(
                                "/", "/main", "/favicon.ico", "/error",

                                //  로그인/회원가입 페이지 및 처리
                                "/auth/login",
                                "/auth/register",

                                //  이메일 코드 인증 관련 (새로운 방식)
                                "/auth/send-code",      // 코드 전송 (메일 발송)
                                "/auth/verify-code",    // 코드 검증 (세션 등록)

                                //  실시간 유효성 검사 (AJAX)
                                "/auth/validate/**",

                                //  정적 리소스 (CSS/JS/이미지 등)
                                "/css/**", "/js/**", "/img/**", "/images/**", "/webjars/**"
                        ).permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                        //.anyRequest().permitAll()
                )

                /* -------------------------------
                 * [3] Authentication Provider 등록
                 * ------------------------------- */
                .authenticationProvider(daoAuthProvider)

                /* -------------------------------
                 * [4] RequestCache 비활성화
                 * -------------------------------
                 * - 이전 요청을 저장하지 않음 (보안상 안전)
                 * - 로그인 후 이전 URL로 자동 리다이렉트 방지
                 */
                .requestCache(rc -> rc.requestCache(new NullRequestCache()))

                /* -------------------------------
                 * [5] 세션 관리 설정
                 * -------------------------------
                 * - 세션이 필요할 때만 생성 (IF_REQUIRED)
                 * - invalidSessionUrl() 제거: 익명 POST가 '/'로 리다이렉트되는 버그 방지
                 */
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                /* -------------------------------
                 * [6] 폼 로그인 설정
                 * -------------------------------
                 * - 사용자 지정 로그인 페이지(/auth/login)
                 * - 로그인 처리 URL 동일 (/auth/login)
                 * - 성공 시 / (메인) 으로 리다이렉트
                 * - 실패 시 로그인 페이지로 다시 이동
                 */
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/auth/login?error")
                        .permitAll()
                )

                /* -------------------------------
                 * [7] 로그아웃 설정
                 * -------------------------------
                 * - 세션 무효화 및 쿠키 삭제
                 * - 로그아웃 성공 후 로그인 페이지로 이동
                 */
                .logout(lo -> lo
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        /* -------------------------------
         * [선택] Remember-Me 기능 (자동 로그인)
         * -------------------------------
         * http.rememberMe(rm -> rm
         *     .userDetailsService(customUserDetailsService)
         *     .tokenValiditySeconds(60 * 60 * 24 * 14) // 14일 유지
         *     .rememberMeParameter("remember-me")
         * );
         */

        return http.build();
    }
}
