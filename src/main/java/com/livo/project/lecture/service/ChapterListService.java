package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.repository.ChapterListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterListService {
    private final ChapterListRepository chapterListRepository;

    public List<ChapterList> getChaptersByLecture(int lectureId) {
        return chapterListRepository.findByLectureIdOrderByChapterOrderAsc(lectureId);
    }
}
