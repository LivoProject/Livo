package com.livo.project.notice.service;

import com.livo.project.admin.domain.dto.NoticeListDto;
import com.livo.project.notice.domain.entity.Notice;
import com.livo.project.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<NoticeListDto> findAllNoticeDtos() {
        return noticeRepository.findNoticeListWithNickname();
    }

    public Notice findNoticeById(int id){
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항 입니다."));
    }

    public List<Notice> getRecentNotices() {
        return noticeRepository.findTop5ByOrderByCreatedAtDesc();
    }
}
