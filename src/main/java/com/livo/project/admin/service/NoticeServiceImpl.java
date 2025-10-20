// src/main/java/com/livo/project/admin/service/NoticeServiceImpl.java
package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.NoticeReq;
import com.livo.project.admin.repository.NoticeRepository;
import com.livo.project.mypage.domain.entity.MypageNotice;   // <- 교체
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repo;

    @Override
    @Transactional(readOnly = true)
    public Page<MypageNotice> list(String q, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                // 엔티티의 실제 필드명 기준 정렬
                Sort.by(Sort.Order.desc("pinned"), Sort.Order.desc("createdAt"))
        );
        if (q == null || q.isBlank()) return repo.findAll(pageable);
        return repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pageable);
    }

    @Override
    public MypageNotice create(NoticeReq req, String adminNameOrEmail) {
        MypageNotice n = new MypageNotice();
        n.setTitle(req.getTitle().trim());
        n.setContent(req.getContent());
        n.setPinned(req.isPinned());        // <- 필드명 pinned
        n.setVisible(req.isVisible());      // <- 필드명 visible
        n.setViewCount(0);
        n.setWriter(adminNameOrEmail != null ? adminNameOrEmail : "관리자");
        return repo.save(n);
    }

    @Override
    @Transactional(readOnly = true)
    public MypageNotice get(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음: " + id));
    }

    @Override
    public MypageNotice update(int id, NoticeReq req) {
        MypageNotice n = get(id);
        n.setTitle(req.getTitle().trim());
        n.setContent(req.getContent());
        n.setPinned(req.isPinned());
        n.setVisible(req.isVisible());
        return n; // dirty checking
    }

    @Override
    public void delete(int id) {
        repo.deleteById(id);
    }

    @Override
    public void increaseView(int id) {
        MypageNotice n = get(id);
        n.setViewCount(n.getViewCount() + 1);
    }
}
