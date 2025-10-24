package com.livo.project.utils;

import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.auth.security.CustomUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

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

}
