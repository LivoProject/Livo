// com.livo.project.auth.security.AppUserDetails.java
package com.livo.project.auth.security;

import com.livo.project.auth.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class AppUserDetails implements UserDetails {
    private final User user;

    /** JSP에서 principal.nickname 으로 표시 */
    public String getNickname() { return user.getNickname(); }
    /** 필요하면 principal.name도 활용 가능 */
    public String getName() { return user.getName(); }
    public Long getId() { return user.getId(); }

    @Override public String getUsername() { return user.getEmail(); }
    @Override public String getPassword() { return user.getPassword(); }

    /** 컨트롤러에서 사용하기 위한 추가 getter */
    public String getEmail() { return user.getEmail(); }
    public String getProvider() { return user.getProvider(); }

    /** roleId → ROLE_* 매핑 */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Integer roleId = user.getRoleId();
        String role = switch (roleId == null ? 1 : roleId) {
            case 3 -> "ADMIN";
            case 2 -> "MANAGER";
            case 1 -> "USER";
            default -> "USER";
        };
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    /** 계정 상태 매핑 (네가 쓰던 정책 유지) */
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return user.getDeletedAt() == null; }

    @Override
    public boolean isEnabled() {
        boolean activeStatus  = (user.getStatus() == null) || Boolean.TRUE.equals(user.getStatus());
        boolean emailVerified = Boolean.TRUE.equals(user.getEmailVerified());
        boolean notDeleted    = (user.getDeletedAt() == null);
        return activeStatus && emailVerified && notDeleted;
    }
}
