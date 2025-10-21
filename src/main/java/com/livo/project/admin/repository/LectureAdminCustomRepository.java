package com.livo.project.admin.repository;

import com.livo.project.admin.domain.dto.LectureSearch;
import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureAdminCustomRepository{
    Page<Lecture> search(LectureSearch search, Pageable pageable);

}
