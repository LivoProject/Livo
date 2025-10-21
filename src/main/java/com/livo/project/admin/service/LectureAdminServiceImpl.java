package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.LectureSearch;
import com.livo.project.admin.repository.LectureAdminCustomRepositoryImpl;
import com.livo.project.admin.repository.LectureAdminRepository;
import com.livo.project.lecture.domain.Category;
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

@Transactional
@RequiredArgsConstructor
@Service
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
    public boolean deleteLecture(int lectureId) {
        if (!lectureRepository.existsById(lectureId)) {
            return false;
        }
        try{
            lectureRepository.deleteById(lectureId);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

}
