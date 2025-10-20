package com.livo.project.mypage.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MypageNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noticeId")
    private int id; // 공지 ID

    @Column(nullable = false, length = 200)
    private String title; // 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 내용

    @Column(length = 50)
    private String writer; // 작성자

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; // 작성일

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정일

    @Column(nullable = false)
    private int viewCount; // 조회수

    @Column(name = "isPinned", nullable = false)
    private boolean pinned;   //  DB의 isPinned와 매핑

    @Column(name = "isVisible", nullable = false)
    private boolean visible;  //  DB의 isVisible과 매핑
}
