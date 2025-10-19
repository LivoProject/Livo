package com.livo.project.maindashboard.repository;

import com.livo.project.maindashboard.domain.entity.MainDashBoardNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainDashBoardNoticeRepository extends JpaRepository<MainDashBoardNotice, Integer> {

    // 공지사항을 'isPinned' 우선 + 최신순(createdAt 내림차순)으로 정렬해서 상위 5개만
    List<MainDashBoardNotice> findTop5ByOrderByIsPinnedDescCreatedAtDesc();
}
