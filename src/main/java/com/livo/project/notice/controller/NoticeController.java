package com.livo.project.notice.controller;

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

    @GetMapping("/list")
    public String noticeList(Model model) {
        List<Notice> notices = noticeService.findAllNotices();
        model.addAttribute("notices", notices);
        return "notice/list";
    }

    @GetMapping("/content")
    public String noticeContent(@RequestParam("id") int id, Model model) {
        Notice notice = noticeService.findNoticeById(id);
        model.addAttribute("notice", notice);
        return "notice/content";
    }

}
