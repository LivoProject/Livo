package com.livo.project.lecture.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.livo.project.payment.domain.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity(name = "LectureEntity")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="lecture")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lectureId;

    private String title;

    @Lob
    private String content;

    private String tutorName;

    @Lob
    private String tutorInfo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime reservationStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime reservationEnd;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lectureStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lectureEnd;

    private Integer totalCount;
    private int reservationCount = 0;
    private int price;

    private Boolean isFree = false;

    @Enumerated(EnumType.STRING)
    private LectureStatus status = LectureStatus.OPEN;  // 기본 OPEN

    private String thumbnailUrl;
    private boolean customThumbnail;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId")
    private Category category;

    public Lecture(Integer lectureId) {
        this.lectureId = lectureId;
    }

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Payment> payments;

    public enum LectureStatus  {
        OPEN,CLOSED,ENDED
    }

}
