// com.livo.project.auth.security.AppUserDetails.java
package com.livo.project.auth.security;

import com.livo.project.auth.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class AppUserDetails implements UserDetails {
    private final User user;

    @Override public String getUsername() { return user.getEmail(); }
    @Override public String getPassword() { return user.getPassword(); }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        // 필요 시 Role 매핑
        return List.of(() -> "ROLE_USER");
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return Boolean.TRUE.equals(user.getEmailVerified()); }
}
