package com.livo.project.main.repository;

import com.livo.project.main.domain.entity.MainNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainNoticeRepository extends JpaRepository<MainNotice, Integer> {

    // 공지사항을 'isPinned' 우선 + 최신순(createdAt 내림차순)으로 정렬해서 상위 5개만
    List<MainNotice> findTop5ByOrderByIsPinnedDescCreatedAtDesc();
}
