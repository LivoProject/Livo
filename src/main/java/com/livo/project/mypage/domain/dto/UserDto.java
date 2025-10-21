package com.livo.project.mypage.domain.dto;

import com.livo.project.auth.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String gender;
    private String role;
    private String createdAt;


    public static com.livo.project.auth.domain.dto.UserDto fromEntity(User user) {

        return com.livo.project.auth.domain.dto.UserDto.builder()
                .id(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(String.valueOf(user.getRoleId()))
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .createdAt(user.getCreatedAt() != null
                        ? user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        : null)
                .build();
    }
}
