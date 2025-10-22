package com.livo.project.lecture.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date reservationStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date reservationEnd;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lectureStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lectureEnd;

    private int totalCount;
    private int reservationCount = 0;
    private int price;

    private Boolean isFree = false;
    private String status;
    private int categoryId;
    private String thumbnailUrl;
    private boolean customThumbnail;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private Category category;
}
