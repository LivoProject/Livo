package com.livo.project.mypage.repository;

import com.livo.project.notice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MypageNoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findTop5ByOrderByCreatedAtDesc();
}
