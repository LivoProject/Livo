package com.livo.project.notice.controller;

import com.livo.project.admin.domain.dto.NoticeListDto;
import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import com.livo.project.notice.domain.entity.Notice;
import com.livo.project.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final UserRepository userRepository;

    // 공지사항 목록
    @GetMapping("/list")
    public String noticeList(Model model) {
        List<NoticeListDto> notices = noticeService.findAllNoticeDtos();
        model.addAttribute("notices", notices);
        return "notice/list";
    }

    // 공지사항 상세
    @GetMapping("/content")
    public String noticeContent(@RequestParam("id") int id, Model model) {
        Notice notice = noticeService.findNoticeById(id);

        //  이메일 기반으로 닉네임 조회
        String nickname = userRepository.findByEmail(notice.getWriter())
                .map(User::getNickname)
                .orElse(notice.getWriter()); // 없으면 이메일 그대로 표시

        model.addAttribute("notice", notice);
        model.addAttribute("nickname", nickname); //
        return "notice/content";
    }
}
