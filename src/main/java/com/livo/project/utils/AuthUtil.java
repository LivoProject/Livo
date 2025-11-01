package com.livo.project.utils;

import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.auth.security.CustomUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthUtil {
    public static String getLoginUserEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()){
            return null;
        }
        Object principal = auth.getPrincipal();
        if(principal instanceof AppUserDetails user){
            return user.getEmail();
        }else if(principal instanceof DefaultOAuth2User oAuth2User){
            return (String) oAuth2User.getAttributes().get("email");
        }
        return null;
    }



    // 이메일 + 프로바이더
    public static Map<String, String> getLoginUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> result = new HashMap<>();

        if (auth == null || !auth.isAuthenticated()) return result;
        Object principal = auth.getPrincipal();

        //  로컬 로그인
        if (principal instanceof AppUserDetails user) {
            result.put("email", user.getEmail());
            result.put("provider", user.getProvider() != null ? user.getProvider().toUpperCase() : "LOCAL");
            return result;
        }

        // OAuth2 로그인 (Google / Naver / Kakao)
        if (principal instanceof DefaultOAuth2User oAuth2User) {
            String email = null;
            String provider = "OAUTH2";

            if (oAuth2User.getAttributes().get("email") != null) {
                email = (String) oAuth2User.getAttributes().get("email");
            } else if (oAuth2User.getAttributes().get("kakao_account") instanceof Map<?, ?> kakao) {
                email = (String) kakao.get("email");
            } else if (oAuth2User.getAttributes().get("response") instanceof Map<?, ?> naver) {
                email = (String) naver.get("email");
            }

            if (auth instanceof OAuth2AuthenticationToken token) {
                provider = token.getAuthorizedClientRegistrationId().toUpperCase();
            }

            result.put("email", email);
            result.put("provider", provider);
        }

        return result;
    }


}
