package com.livo.project.admin.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeReq {

    /** 제목 (VARCHAR(255) NOT NULL DEFAULT '') */
    @NotBlank
    @Size(max = 255)
    private String title;

    /** 내용 (TEXT NOT NULL DEFAULT '') */
    @NotBlank
    private String content;

    /** 상단 고정 여부 (isPinned) */
    private boolean isPinned;

    /** 노출 여부 (isVisible) */
    private boolean isVisible;
}
