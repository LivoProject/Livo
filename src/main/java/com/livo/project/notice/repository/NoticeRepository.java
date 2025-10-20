package com.livo.project.notice.repository;

import com.livo.project.notice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository  extends JpaRepository<Notice, Integer> {
    List<Notice> findAllByOrderByCreatedAtDesc();
}
