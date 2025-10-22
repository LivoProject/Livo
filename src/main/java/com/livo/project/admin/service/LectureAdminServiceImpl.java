package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.LectureRequest;
import com.livo.project.admin.domain.dto.LectureSearch;
import com.livo.project.admin.repository.LectureAdminCustomRepositoryImpl;
import com.livo.project.admin.repository.LectureAdminRepository;
import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.ChapterList;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.CategoryRepository;
import com.livo.project.lecture.repository.ChapterListRepository;
import com.livo.project.lecture.repository.LectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureAdminServiceImpl implements LectureAdminService {

    private final CategoryRepository categoryRepository;
    private final LectureRepository lectureRepository;
    private final ChapterListRepository chapterListRepository;
    private final LectureAdminCustomRepositoryImpl lectureCustomRepository;
    private final LectureAdminRepository lectureAdminRepository;

    @Override
    public Lecture saveLecture(Lecture lecture, int categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 카테고리 입니다."));
        lecture.setCategory(category);
        if(lecture.getIsFree()){
            lecture.setPrice(0);
        }
        return lectureRepository.save(lecture);

    }

    @Override
    public void deleteLecture(int lectureId) {
        // 1. 관련 챕터 먼저 삭제 (외래키 제약 방지)
        chapterListRepository.deleteByLecture_LectureId(lectureId);

        // 2. 강의 삭제
        lectureRepository.deleteById(lectureId);
    }

    @Override
    public Lecture editLecture(int lectureId) {
        return lectureRepository.findById(lectureId).orElseThrow(()-> new IllegalArgumentException("강의가 존재하지 않습니다."));
    }

    @Override
    public Lecture updateLecture(Lecture updateLecture, int categoryId) {
        Lecture existingLecture =  lectureRepository.findById(updateLecture.getLectureId())
                .orElseThrow(()-> new IllegalArgumentException("강의를 찾을 수 없습니다."));
        existingLecture.setTitle(updateLecture.getTitle());
        existingLecture.setTutorName(updateLecture.getTutorName());
        existingLecture.setTutorName(updateLecture.getTutorInfo());
        existingLecture.setContent(updateLecture.getContent());
        existingLecture.setTotalCount(updateLecture.getTotalCount());
        existingLecture.setLectureStart(updateLecture.getLectureStart());
        existingLecture.setLectureEnd(updateLecture.getLectureEnd());
        existingLecture.setReservationStart(updateLecture.getReservationStart());
        existingLecture.setReservationEnd(updateLecture.getReservationEnd());
        existingLecture.setIsFree(updateLecture.getIsFree());
        existingLecture.setPrice(updateLecture.getIsFree() ? 0 : updateLecture.getPrice());

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 카테고리 입니다."));
        existingLecture.setCategory(category);

        return lectureRepository.save(existingLecture);
    }

    @Override
    public Lecture findById(int lectureId) {
        return lectureRepository.findById(lectureId).orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
    }

    @Override
    public Page<Lecture> searchLecture(LectureSearch search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lectureCustomRepository.search(search, pageable);
    }

    @Override
    public List<Lecture> getRecentLectures() {
        return lectureAdminRepository.findTop5ByOrderByLectureIdDesc();
    }

    @Override
    public Lecture saveOrUpdateLecture(LectureRequest request, int categoryId) {
        Lecture lecture = request.getLecture();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        lecture.setCategory(category);
        // 무료 강의 처리
        if (lecture.getIsFree()) {
            lecture.setPrice(0);
        }

        // 저장 (등록 or 수정)
        Lecture savedLecture = lectureRepository.save(lecture);

        // 챕터 저장 로직
        List<ChapterList> chapters = request.getChapters();
        if (chapters != null && !chapters.isEmpty()) {
            for (ChapterList c : chapters) {
                c.setLectureId(savedLecture.getLectureId());
            }
            chapterListRepository.saveAll(chapters);
            updateLectureThumbnail(savedLecture.getLectureId(), chapters);
        }

        return savedLecture;
    }
    private void updateLectureThumbnail(int lectureId, List<ChapterList> chapters) {
        if (chapters == null || chapters.isEmpty()) return;

        String firstUrl = chapters.get(0).getYoutubeUrl();
        String videoId = extractVideoId(firstUrl);
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
