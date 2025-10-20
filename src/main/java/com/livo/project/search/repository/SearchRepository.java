package com.livo.project.search.repository;

import com.livo.project.maindashboard.domain.entity.MainDashBoardLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SearchRepository extends JpaRepository<MainDashBoardLecture, Long> {


    List<MainDashBoardLecture> findByTitleContainingIgnoreCase(String title);
    List<MainDashBoardLecture> findByContentContainingIgnoreCase(String content);
    List<MainDashBoardLecture> findByTutorNameContainingIgnoreCase(String tutorName);
}
