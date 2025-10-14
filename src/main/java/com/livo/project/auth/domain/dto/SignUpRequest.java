package com.livo.project.auth.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SignUpRequest {
    @NotBlank @Email @Size(max = 254)
    private String email;

    @NotBlank @Size(min = 8, max = 64)
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,64}$",
            message="비밀번호는 대/소문자·숫자·특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank @Size(min = 2, max = 50)
    private String name;

    @NotBlank
    @Pattern(regexp="^[\\p{L}\\p{N}_]{2,40}$", message="닉네임은 2~40자, 특수문자 제외(_만 허용)")
    private String nickname;

    @Pattern(regexp="^$|^(\\+\\d{8,15}|0\\d{8,10}|\\d{8,11})$", message="전화번호 형식이 올바르지 않습니다.")
    private String phone;

    @Pattern(regexp="^$|^\\d{4}-\\d{2}-\\d{2}$", message="생년월일은 yyyy-MM-dd 형식")
    private String birth;

    @Pattern(regexp="^$|^(M|F)$", message="성별은 M 또는 F")
    private String gender;
}
