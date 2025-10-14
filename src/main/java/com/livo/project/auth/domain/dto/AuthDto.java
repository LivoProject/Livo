package com.livo.project.auth.domain.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class AuthDto {

    public enum Gender { M, F }

    /** 회원가입 요청 DTO */
    public static class SignUpRequest {
        @NotBlank @Email @Size(max = 254)
        private String email;

        @NotBlank @Size(min = 8, max = 64)
        private String password;

        @NotBlank @Size(min = 2, max = 50)
        private String name;

        // 선택값이라면 @NotBlank 제거
        @Size(min = 2, max = 40)
        private String nickname;

        // 선택값: +, 숫자, - 허용 / 최대 16자
        @Pattern(regexp = "^$|^[+0-9][0-9\\-]{0,15}$", message = "전화번호 형식이 올바르지 않습니다.")
        private String phone;

        // 선택값: 날짜는 LocalDate로 받고 과거 날짜만 허용
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        private LocalDate birth;

        // 선택값: Enum으로 안전하게
        private Gender gender;

        // getters/setters ...
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public LocalDate getBirth() { return birth; }
        public void setBirth(LocalDate birth) { this.birth = birth; }
        public Gender getGender() { return gender; }
        public void setGender(Gender gender) { this.gender = gender; }
    }

    /** 로그인 요청 DTO */
    public static class LoginRequest {
        @NotBlank @Email private String email;
        @NotBlank private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /** 내 정보 응답 DTO */
    public static class MeResponse {
        private String email, name, nickname, phone;
        public MeResponse(String email, String name, String nickname, String phone) {
            this.email = email; this.name = name; this.nickname = nickname; this.phone = phone;
        }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getNickname() { return nickname; }
        public String getPhone() { return phone; }
    }
}
