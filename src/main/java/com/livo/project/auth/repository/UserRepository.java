package com.livo.project.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.livo.project.auth.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);
    Optional<User> findByEmail(String email);
}
