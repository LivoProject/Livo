// com.livo.project.mypage.dto/ProfileUpdateRequest.java
package com.livo.project.mypage.dto;
import jakarta.validation.constraints.*;

public record ProfileUpdateRequest(
        @NotBlank @Size(min=2, max=40) String nickname
) {}
