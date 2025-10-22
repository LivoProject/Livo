package com.livo.project.admin.service;

import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.ChapterListRepository;
import com.livo.project.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ChapterAdminService {
    private final ChapterListRepository chapterListRepository;
    private final LectureRepository lectureRepository;

    public void deleteById(int chapterId) {
        chapterListRepository.deleteById(chapterId);
    }

    public void save(List<ChapterList> chapters) {
        chapterListRepository.saveAll(chapters);
    }

    public List<ChapterList> getChaptersByLecture(int lectureId) {
        return chapterListRepository.findByLectureIdOrderByChapterOrderAsc(lectureId);
    }








}
