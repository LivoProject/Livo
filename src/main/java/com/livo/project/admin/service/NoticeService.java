// src/main/java/com/livo/project/admin/service/NoticeService.java
package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.NoticeReq;
import com.livo.project.mypage.domain.entity.MypageNotice;   // <- 교체
import org.springframework.data.domain.Page;

public interface NoticeService {
    Page<MypageNotice> list(String q, int page, int size);
    MypageNotice create(NoticeReq form, String adminNameOrEmail);
    MypageNotice get(int id);
    MypageNotice update(int id, NoticeReq form);
    void delete(int id);
    void increaseView(int id);
}
