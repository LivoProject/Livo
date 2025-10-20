package com.livo.project.admin.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeReq {
    @NotBlank @Size(max = 255)
    private String title;

    @NotBlank
    private String content;

    private boolean pinned;   // -> isPinned
    private boolean visible;  // -> isVisible
}
