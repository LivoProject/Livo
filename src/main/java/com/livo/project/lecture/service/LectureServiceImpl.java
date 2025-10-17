package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.LectureRepository;
import com.livo.project.lecture.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final CategoryRepository categoryRepository;

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
    public List<Lecture> findAll() {
        return lectureRepository.findAll();
    }

    @Override
    public Optional<Lecture> findById(int lectureId) {
        return lectureRepository.findById(lectureId);
    }

    @Override
    public List<Lecture> findByCategoryId(int categoryId) {
        return lectureRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Lecture> findByTitleContaining(String keyword) {
        return lectureRepository.findByTitleContaining(keyword);
    }

    // ✅ 페이징용: 페이징은 나중에 다시 정리!!
    @Override
    public Page<Lecture> getLecturePage(Pageable pageable) {
        return lectureRepository.findAll(pageable);
    }

    // ✅ 검색 + 페이징 조합: 이것도 나중에 다시 정리!!
    @Override
    public Page<Lecture> searchLecturePage(String keyword, Pageable pageable) {
        return lectureRepository.findByTitleContaining(keyword, pageable);
    }
}
