package com.livo.project.search.controller;

import com.livo.project.search.domain.dto.SearchDto;
import com.livo.project.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    @GetMapping(produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<SearchDto> search(@RequestParam String keyword) {
        return searchService.searchLectures(keyword);
    }
}
