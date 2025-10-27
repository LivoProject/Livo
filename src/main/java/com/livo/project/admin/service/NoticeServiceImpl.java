// src/main/java/com/livo/project/admin/service/NoticeServiceImpl.java
package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.NoticeReq;
import com.livo.project.notice.repository.NoticeRepository;   //  공용 레포 사용
import com.livo.project.notice.domain.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repo;

    /** 공지 목록: isPinned 우선 + 최신순, q가 있으면 제목/내용 검색 */
    @Override
    @Transactional(readOnly = true)
    public Page<Notice> list(String q, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdAt"))
        );

        if (q == null || q.isBlank()) {
            return repo.findAll(pageable);
        }
        return repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pageable);
    }

    /** 공지 생성 */
    @Override
    public Notice create(NoticeReq req, String adminNameOrEmail) {
        Notice n = Notice.builder()
                .title(req.getTitle().trim())
                .content(req.getContent())
                .writer(adminNameOrEmail != null ? adminNameOrEmail : "관리자")
                .isPinned(req.isPinned())
                .isVisible(req.isVisible())
                .viewCount(0)
                .build();
        return repo.save(n);
    }

    /** 단건 조회 */
    @Override
    @Transactional(readOnly = true)
    public Notice get(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음: " + id));
    }

    /** 수정 */
    @Override
    public Notice update(int id, NoticeReq req) {
        Notice n = get(id);
        n.setTitle(req.getTitle().trim());
        n.setContent(req.getContent());
        n.setPinned(req.isPinned());
        n.setVisible(req.isVisible());
        return n; // dirty checking
    }

    /** 삭제 */
    @Override
    public void delete(int id) {
        repo.deleteById(id);
    }

    /** 조회수 +1 */
    @Override
    public void increaseView(int id) {
        Notice n = get(id);
        n.setViewCount(n.getViewCount() + 1);
    }

    @Override
    public void toggleVisible(int id) {
        Notice n = get(id);
        n.setVisible(!n.isVisible());
    }

    @Override
    public void togglePin(int id) {
        Notice n = get(id);
        n.setPinned(!n.isPinned());
    }
}
