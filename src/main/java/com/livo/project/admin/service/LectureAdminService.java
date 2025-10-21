package com.livo.project.admin.service;

import com.livo.project.lecture.domain.Lecture;

import java.util.Optional;

public interface LectureAdminService {
    Lecture saveLecture(Lecture lecture, int categoryId);

    boolean deleteLecture(int lectureId);

    Lecture editLecture(int lectureId);

    Lecture updateLecture(Lecture updateLecture, int categoryId);

    // 강좌 상세 조회 (PK 기준)
    Lecture findById(int lectureId);


}
