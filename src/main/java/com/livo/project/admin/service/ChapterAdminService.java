package com.livo.project.admin.service;

import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.ChapterListRepository;
import com.livo.project.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChapterAdminService {
    private final ChapterListRepository chapterListRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public void deleteById(int chapterId) {
        chapterListRepository.deleteById(chapterId);
    }

    @Transactional
    public void save(List<ChapterList> chapters) {
        chapterListRepository.saveAll(chapters);
        updateLectureThumbnail(chapters);
    }

    public List<ChapterList> getChaptersByLecture(int lectureId) {
        return chapterListRepository.findByLectureIdOrderByChapterOrderAsc(lectureId);
    }

    private void updateLectureThumbnail(List<ChapterList> chapters) {
        if (chapters == null || chapters.isEmpty()) return;

        int lectureId = chapters.get(0).getLectureId();
        List<ChapterList> savedChapters = chapterListRepository.findByLectureIdOrderByChapterOrderAsc(lectureId);

        if (savedChapters.isEmpty()) return;

        String firstUrl = savedChapters.get(0).getYoutubeUrl();
        String videoId = extractVideoId(firstUrl);
        if (videoId == null) return;

        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found: " + lectureId));

        lecture.setThumbnailUrl(thumbnailUrl);
        lectureRepository.save(lecture);
    }

    private String extractVideoId(String url) {
        if (url == null) return null;
        if (url.contains("v=")) return url.split("v=")[1].split("&")[0];
        if (url.contains("youtu.be/")) return url.split("youtu.be/")[1];
        return null;
    }




}
