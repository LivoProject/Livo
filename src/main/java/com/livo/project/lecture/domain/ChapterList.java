package com.livo.project.lecture.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="chapter_list")
public class ChapterList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chapterId;

    private String chapterName;
    private int chapterOrder;
    private int lectureId;
    private String youtubeUrl;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "lectureId", insertable = false, updatable = false)
//    private Lecture lecture;

    @Lob
    private String content;
}
