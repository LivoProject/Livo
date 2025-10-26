package com.livo.project.mypage.domain.entity;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.lecture.domain.Lecture;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int progressId;

    // 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lectureId")
    private Lecture lecture;


    @Column(nullable = false)
    private String email; // 진행률 저장용

    // 시청 정보
    private double progressPercent; //진도율
    private double lastWatchedTime; //마지막 시청 위치
    private LocalDateTime lastAccessedAt;


    public LectureProgress(String email, Lecture lecture) {
        this.email = email;
        this.lecture = lecture;
    }
}
