package com.livo.project.main.domain;

import com.livo.project.lecture.domain.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture")
@Getter @Setter
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lectureId;

    private String title;
    private String content;
    private String tutorName;
    private String tutorInfo;
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;
    private LocalDateTime lectureStart;
    private LocalDateTime lectureEnd;
    private int totalCount;
    private int reservationCount;
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;
}
