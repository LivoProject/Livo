// src/main/java/com/livo/project/admin/service/NoticeServiceImpl.java
package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.NoticeListDto;
import com.livo.project.admin.domain.dto.NoticeReq;
import com.livo.project.notice.domain.entity.Notice;
import com.livo.project.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repo;

    /** 엔티티 Page 반환 (기존 호환용) */
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

    /** 관리자용: 닉네임 포함 DTO Page 반환 */
    @Override
    @Transactional(readOnly = true)
    public Page<NoticeListDto> adminList(String q, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdAt"))
        );
        return repo.findAdminList(q, pageable); // 반드시 Repository에 존재해야 함
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

    /** 공개 토글 */
    @Override
    public void toggleVisible(int id) {
        Notice n = get(id);
        n.setVisible(!n.isVisible());
    }

    /** 상단 고정 토글 */
    @Override
    public void togglePin(int id) {
        Notice n = get(id);
        n.setPinned(!n.isPinned());
    }
}
