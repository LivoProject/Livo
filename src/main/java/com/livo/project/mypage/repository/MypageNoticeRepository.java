package com.livo.project.mypage.repository;

import com.livo.project.mypage.domain.entity.MypageNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MypageNoticeRepository extends JpaRepository<MypageNotice, Long> {

    // 공지사항을 'isPinned' 우선 + 최신순(createdAt 내림차순)으로 정렬해서 상위 5개만
    List<MypageNotice> findTop5ByOrderByIsPinnedDescCreatedAtDesc();
}
