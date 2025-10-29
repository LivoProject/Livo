package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.LectureRequest;
import com.livo.project.admin.domain.dto.LectureSearch;
import com.livo.project.admin.domain.dto.LectureUpdateRequest;
import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface LectureAdminService {
    Lecture saveLecture(Lecture lecture, int categoryId);

    void deleteLecture(int lectureId);

    Lecture editLecture(int lectureId);

    Lecture updateLecture(LectureUpdateRequest ureq, int categoryId);

    // 강좌 상세 조회 (PK 기준)
    Lecture findById(int lectureId);

    Page<Lecture> searchLecture(LectureSearch search, int page, int pageSize);

    List<Lecture> getRecentLectures();

    Lecture saveLectureAjax(LectureRequest request, int categoryId);

}
