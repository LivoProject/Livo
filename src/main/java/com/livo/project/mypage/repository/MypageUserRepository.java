package com.livo.project.mypage.repository;

import com.livo.project.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MypageUserRepository extends JpaRepository<User, Long> {
        boolean existsByEmail(String email);
        boolean existsByNickname(String nickname);
        boolean existsByPhone(String phone);
        Optional<User> findByEmailAndProvider(String email, String provider);

        boolean existsByEmailAndProvider(String email, String provider);

}
