package com.livo.project.lecture.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
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

    private Date reservationStart;
    private Date reservationEnd;
    private Date lectureStart;
    private Date lectureEnd;

    private int totalCount;
    private int reservationCount = 0;
    private int price;

    private int categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private Category category;

}
