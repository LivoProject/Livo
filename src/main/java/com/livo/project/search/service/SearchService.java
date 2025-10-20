package com.livo.project.search.service;

import com.livo.project.maindashboard.domain.dto.MainDashBoardLectureDto;
import com.livo.project.maindashboard.domain.entity.MainDashBoardLecture;
import com.livo.project.maindashboard.service.MainDashBoardService;
import com.livo.project.search.domain.dto.SearchDto;
import com.livo.project.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MainDashBoardService mainDashBoardService;

    public List<SearchDto> searchLectures(String keyword) {
        List<MainDashBoardLectureDto> allLectures = mainDashBoardService.getAllLectureDtos();

        return allLectures.stream()
                .filter(l -> l.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || l.getTutorName().toLowerCase().contains(keyword.toLowerCase())
                        || l.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .map(l -> new SearchDto(
                        l.getLectureId(),
                        l.getTitle(),
                        l.getTutorName(),
                        l.getContent(),
                        l.getThumbnail(),
                        l.getPrice()))
                .toList();
    }
}
