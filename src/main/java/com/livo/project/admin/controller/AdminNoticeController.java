// src/main/java/com/livo/project/admin/controller/AdminNoticeController.java
package com.livo.project.admin.controller;

import com.livo.project.admin.domain.dto.NoticeReq;
import com.livo.project.admin.service.NoticeService;
import com.livo.project.mypage.domain.entity.MypageNotice;   // <- 교체
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/notice")
public class AdminNoticeController {

    private final NoticeService service;

    @GetMapping("/list")
    public String list(@RequestParam(value="q", required=false) String q,
                       @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value="size", defaultValue="10") int size,
                       Model model) {
        Page<MypageNotice> result = service.list(q, page, size);  // <- 교체
        model.addAttribute("page", result);
        model.addAttribute("q", q == null ? "" : q);
        return "admin/noticePage";
    }

    @GetMapping("/new")
    public String createForm() { return "admin/noticeform"; }

    @PostMapping
    public String create(@Valid @ModelAttribute NoticeReq form, Authentication auth) {
        service.create(form, (auth != null ? auth.getName() : "관리자"));
        return "redirect:/admin/notice/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        MypageNotice n = service.get(id);  // <- 교체
        model.addAttribute("n", n);
        return "admin/noticeform";
    }

    @PostMapping(value="/{id}", params="_method=PUT")
    public String update(@PathVariable int id, @Valid @ModelAttribute NoticeReq form) {
        service.update(id, form);
        return "redirect:/admin/notice/list";
    }

    @PostMapping(value="/{id}", params="_method=DELETE")
    public String delete(@PathVariable int id) {
        service.delete(id);
        return "redirect:/admin/notice/list";
    }
}
