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
        System.out.println("videoId: " + videoId);
        if (videoId == null) return;

        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found: " + lectureId));

        lecture.setThumbnailUrl(thumbnailUrl);
        lectureRepository.save(lecture);
    }

    private String extractVideoId(String url) {
        if (url == null || url.isEmpty()) return null;
        try {
            if (url.contains("watch?v=")) {
                String idPart = url.substring(url.indexOf("watch?v=") + 8);
                return idPart.split("[&?]")[0]; // ?si= 등 제거
            }
            if (url.contains("youtu.be/")) {
                String idPart = url.substring(url.indexOf("youtu.be/") + 9);
                return idPart.split("[&?]")[0];
            }
            if (url.contains("embed/")) {
                String idPart = url.substring(url.indexOf("embed/") + 6);
                return idPart.split("[&?]")[0];
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





}
