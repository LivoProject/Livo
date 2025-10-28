package com.livo.project.admin.domain.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeListDto {
    private Integer id;
    private String  title;
    private String  nickname;
    private LocalDateTime createdAt;
    private boolean pinned;
    private boolean visible;
    private int viewCount;
    private String content;

    public Date getCreatedAtAsDate() {
        if (createdAt == null) return null;
        return Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }
}

