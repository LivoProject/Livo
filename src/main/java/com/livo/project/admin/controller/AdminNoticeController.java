package com.livo.project.admin.controller;

import com.livo.project.admin.domain.dto.NoticeListDto;
import com.livo.project.admin.domain.dto.NoticeReq;
import com.livo.project.admin.service.NoticeService;
import com.livo.project.notice.domain.entity.Notice;
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

    /** /admin/notice -> /admin/notice/list 리다이렉트 */
    @GetMapping("")
    public String redirectRootToList() {
        return "redirect:/admin/notice/list";
    }

    /** 공지 목록 (관리자 전용: 닉네임/고정/노출 포함) */
    @GetMapping("/list")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       Model model) {

        Page<NoticeListDto> result = service.adminList(q, page, size); // ← 변경!

        int startNo = (int) (result.getTotalElements() - (long) page * size);
        model.addAttribute("page", result);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("startNo", startNo);
        return "admin/noticePage";
    }
    /** 공지 등록 폼 */
    @GetMapping("/new")
    public String createForm() {
        return "admin/noticeform";
    }

    /** 공지 등록 */
    @PostMapping
    public String create(@Valid @ModelAttribute NoticeReq form, Authentication auth) {
        service.create(form, (auth != null ? auth.getName() : "관리자"));
        return "redirect:/admin/notice/list";
    }

    /** 공지 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        Notice n = service.get(id);
        model.addAttribute("n", n);
        return "admin/noticeform";
    }

    /** 공지 수정 */
    @PostMapping(value = "/{id}", params = "_method=PUT")
    public String update(@PathVariable int id, @Valid @ModelAttribute NoticeReq form) {
        service.update(id, form);
        return "redirect:/admin/notice/list";
    }

    /** 공지 삭제 */
    @PostMapping(value = "/{id}", params = "_method=DELETE")
    public String delete(@PathVariable int id) {
        service.delete(id);
        return "redirect:/admin/notice/list";
    }

    @PostMapping("/visible/{id}")
    @ResponseBody
    public void toggleVisible(@PathVariable int id) {
        service.toggleVisible(id);
    }

    @PostMapping("/pin/{id}")
    @ResponseBody
    public void togglePin(@PathVariable int id) {
        service.togglePin(id);
    }
}
