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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.ServletException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler; //add social


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /**  사용자 인증 로직 구현체 (CustomUserDetailsService 사용) */
    private final UserDetailsService customUserDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomOidcUserService   customOidcUserService;


    /**  비밀번호 암호화용 BCrypt 인코더 (DB의 해시와 동일한 알고리즘 사용) */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Audio
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler();
    }*/


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
    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest req,
                                                HttpServletResponse res,
                                                Authentication auth)
                    throws IOException, ServletException {

                var set = AuthorityUtils.authorityListToSet(auth.getAuthorities());

                //  역할별 세션 시간(초)
                int adminSecs   = 15 * 60;  // 관리자 15분
                int managerSecs = 30 * 60;  // 매니저 30분
                int userSecs    = 60 * 60;  // 일반 60분

                // 세션 확보 후 역할별로 만료 시간 설정
                var session = req.getSession(true); // 없으면 생성
                if (set.contains("ROLE_ADMIN")) {
                    session.setMaxInactiveInterval(adminSecs);
                } else if (set.contains("ROLE_MANAGER")) {
                    session.setMaxInactiveInterval(managerSecs);
                } else {
                    session.setMaxInactiveInterval(userSecs);
                }

                // (저장된 요청 체크를 쓰지 않는 구성이라면 바로 역할별 리다이렉트)
                if (set.contains("ROLE_ADMIN")) {
                    getRedirectStrategy().sendRedirect(req, res, "/admin/dashboard");
                } else if (set.contains("ROLE_MANAGER")) {
                    getRedirectStrategy().sendRedirect(req, res, "/manager");
                } else {
                    //  일반 사용자: 메인으로 보낼 때 '한 번만' 브금 재생 플래그 세팅
                    req.getSession(true).setAttribute("PLAY_BGM_ONCE", Boolean.TRUE);
                    getRedirectStrategy().sendRedirect(req, res, "/");
                }
            }
        };
    }
    /**------------ social login ------------ */
    @Bean
    public AuthenticationFailureHandler oauthFailureHandler() {
        return (request, response, ex) -> {
            // 이메일 충돌 시 사용자 선택 페이지로
            if (ex instanceof org.springframework.security.oauth2.core.OAuth2AuthenticationException e
                    && "account_conflict".equals(e.getError().getErrorCode())) {
                response.sendRedirect("/auth/link-account");
            } else {
                response.sendRedirect("/auth/login?error");
            }
        };
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
                                "/", "/main", "/home", "/index",    // 홈, 메인
                                "/lecture", "/lecture/**",          // 강좌 목록/상세 페이지
                                "/faq-page", "/faq-page/**",            // 공지 목록/상세 페이지
                                "/notice", "/notice/**",            // faq 목록/상세 페이지
                                "/api/lectures/**", "/api/courses/**", // 강좌 조회 API(GET 요청용)
                                "/favicon.ico", "/error",           // 에러, 파비콘

                                //  로그인/회원가입 페이지 및 처리
                                "/auth/login",
                                "/auth/register",

                                //  이메일 코드 인증 관련 (새로운 방식)
                                "/auth/send-code",      // 코드 전송 (메일 발송)
                                "/auth/verify-code",    // 코드 검증 (세션 등록)

                                //  실시간 유효성 검사 (AJAX)
                                "/auth/validate/**",

                                // 소셜 로그인
                                "/oauth2/**", "/login/oauth2/**",

                                //계정 연동 페이지
                                "/auth/link-account", "/auth/link-account/**",

                                //오디오
                                "/audio/**",

                                //  정적 리소스 (CSS/JS/이미지 등)
                                "/css/**", "/js/**", "/img/**", "/images/**", "/webjars/**"
                        ).permitAll()

                                .requestMatchers("/admin/**").hasRole("ADMIN")          //  추가
                                .requestMatchers("/manager/**").hasAnyRole("ADMIN","MANAGER") // 추가


                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                       // .anyRequest().permitAll()
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
                 * [6] OAuth2 소셜 로그인 설정
                 * -------------------------------
                 * - Google, Kakao, Naver 등의 소셜 로그인 활성화
                 * - 성공 시 기존 roleBasedSuccessHandler 사용
                 * - 실패 시 oauthFailureHandler (계정 연결 플로우)
                 */
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/login")
                        .userInfoEndpoint(u -> u
                                .oidcUserService(customOidcUserService) // ★ OIDC(Google openid) 경로
                                .userService(customOAuth2UserService)   // ★ OAuth2(pure) 경로
                        )
                        .successHandler(roleBasedSuccessHandler())
                        .failureHandler(oauthFailureHandler())
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
                        //.defaultSuccessUrl("/", true)
                        .successHandler(roleBasedSuccessHandler())
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
