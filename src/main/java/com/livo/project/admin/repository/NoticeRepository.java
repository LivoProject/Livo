// src/main/java/com/livo/project/admin/repository/NoticeRepository.java
package com.livo.project.admin.repository;

import com.livo.project.mypage.domain.entity.MypageNotice;   // <- 여기만 교체
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<MypageNotice, Integer> {
    Page<MypageNotice> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content, Pageable pageable);
}
