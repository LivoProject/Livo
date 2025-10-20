package com.livo.project.notice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import jakarta.persistence.Table;
@Entity
@Table(name = "notice")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noticeId")
    private Integer id;

    @Builder.Default
    @Column(length = 255, nullable = false)
    private String title = "";

    @Builder.Default
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content = "";

    @Builder.Default
    @Column(length = 50)
    private String writer = "관리자"; // @PrePersist에서 보강

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private int viewCount = 0;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isPinned = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isVisible = true;

    /* ====== JSP용 보조 게터 (fmt:formatDate 호환) ====== */
    public Date getCreatedAtAsDate() {
        if (createdAt == null) return null;
        return Date.from(createdAt.atZone(ZoneId.of("Asia/Seoul")).toInstant());
    }

    public Date getUpdatedAtAsDate() {
        if (updatedAt == null) return null;
        return Date.from(updatedAt.atZone(ZoneId.of("Asia/Seoul")).toInstant());
    }

    @PrePersist
    public void applyDefaults() {
        if (this.writer == null || this.writer.isBlank()) this.writer = "관리자";
        if (this.title == null) this.title = "";
        if (this.content == null) this.content = "";
    }

    /** 조회수 증가 */
    public void increaseViewCount() {
        this.viewCount++;
    }
}
