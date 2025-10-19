// com.livo.project.mypage.dto/PasswordChangeRequest.java
package com.livo.project.mypage.domain.dto;
import jakarta.validation.constraints.*;

public record PasswordChangeRequest(
        @NotBlank String currentPassword,
        @NotBlank @Pattern(
                regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-]).{8,64}$",
                message="영문/숫자/특수문자 포함 8자 이상")
        String newPassword
) {}
