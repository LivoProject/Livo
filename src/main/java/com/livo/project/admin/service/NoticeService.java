// src/main/java/com/livo/project/admin/service/NoticeService.java
package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.NoticeListDto;
import com.livo.project.admin.domain.dto.NoticeReq;
import com.livo.project.notice.domain.entity.Notice;
import org.springframework.data.domain.Page;

public interface NoticeService {
    Page<Notice> list(String q, int page, int size);
    Notice create(NoticeReq form, String adminNameOrEmail);
    Notice get(int id);
    Notice update(int id, NoticeReq form);
    void delete(int id);
    void increaseView(int id);
    void toggleVisible(int id);
    void togglePin(int id);
    Page<NoticeListDto> adminList(String q, int page, int size);
}
