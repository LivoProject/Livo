package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.ChapterList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChapterListRepository extends JpaRepository<ChapterList, Integer> {
    List<ChapterList> findByLectureIdOrderByChapterOrderAsc(int lectureId);

}
