package com.livo.project.report.repository;

import com.livo.project.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByReviewUId(int reviewUId);
}
