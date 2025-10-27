package com.livo.project.notice.repository;

import com.livo.project.notice.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    // [기존 사용처 호환] 최신순
    List<Notice> findAllByOrderByCreatedAtDesc();

    // [신규 권장] 상단 고정 우선 + 최신순
    List<Notice> findAllByOrderByIsPinnedDescCreatedAtDesc();

    // [관리자 검색용] 제목/내용 LIKE, 페이징
    Page<Notice> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content, Pageable pageable);

    // (옵션) 메인 상단 5개만 뽑을 때
    List<Notice> findTop5ByIsVisibleTrueOrderByIsPinnedDescCreatedAtDesc();

    List<Notice> findTop5ByOrderByCreatedAtDesc();
}
