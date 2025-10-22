package com.livo.project.admin.repository;

import com.livo.project.admin.domain.dto.LectureSearch;
import com.livo.project.lecture.domain.Category;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.QLecture;
import com.livo.project.lecture.repository.CategoryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureAdminCustomRepositoryImpl implements LectureAdminCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<Lecture> search(LectureSearch search, Pageable pageable) {
        QLecture lecture = QLecture.lecture;
        BooleanBuilder builder = new BooleanBuilder();

        //카테고리
        if(search.getCategoryId() != null){
            List<Integer> childCategoryIds = new ArrayList<>(
                    categoryRepository.findByParent_CategoryId(search.getCategoryId())
                    .stream()
                    .map(Category::getCategoryId)
                    .toList()
            );
            childCategoryIds.add(search.getCategoryId());

            if(!childCategoryIds.isEmpty()){
                builder.and(lecture.category.categoryId.in(childCategoryIds));
            }
        }
        //키워드
        if(search.getKeyword() != null && !search.getKeyword().isBlank()){
            builder.and(
                    lecture.title.containsIgnoreCase(search.getKeyword())
                            .or(lecture.tutorName.containsIgnoreCase(search.getKeyword()))
            );
        }
        //가격
        if("free".equals(search.getPriceType())){
            builder.and(lecture.price.eq(0));
        } else if ("paid".equals(search.getPriceType())) {
            builder.and(lecture.price.gt(0));
        }
        //상태
        if(search.getStatus()!= null && !search.getStatus().isBlank()){
            builder.and(lecture.status.eq(search.getStatus()));
        }
        //기간
        if(search.getLectureStartDate() != null && search.getLectureEndDate() != null){
            Date start = search.getLectureStartDate();
            Date end = new Date(search.getLectureEndDate().getTime() + (24 * 60 * 60 * 1000) - 1);
            builder.and(lecture.lectureStart.between(start, end));
        }
        if(search.getReservationStartDate() != null && search.getReservationEndDate() != null){
            Date start = search.getReservationStartDate();
            Date end = new Date(search.getReservationEndDate().getTime() +(24*60*60*1000)-1);
            builder.and(
                    lecture.reservationStart.between(start, end));
        }

        long total = queryFactory
                .select(lecture.count())
                .from(lecture)
                .where(builder)
                .fetchOne();

        List<Lecture> content = queryFactory
                .selectFrom(lecture)
                .where(builder)
                .orderBy(lecture.lectureId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(content, pageable, total);
    }
}
