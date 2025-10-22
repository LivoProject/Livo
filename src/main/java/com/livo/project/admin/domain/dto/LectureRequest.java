package com.livo.project.admin.domain.dto;

import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import lombok.Data;

import java.util.List;

@Data
public class LectureRequest {
    private Lecture lecture;
    private List<ChapterList> chapters;
}
