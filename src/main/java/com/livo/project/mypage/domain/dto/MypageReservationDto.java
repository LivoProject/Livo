package com.livo.project.mypage.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MypageReservationDto {

    // Reservation 기본 정보
    private Integer reservationId;
    private String email;
    private Integer lectureId;
    private LocalDateTime createdAt;  // DTO는 LocalDateTime 유지
    private String status;

    // Lecture에서 가져올 정보
    private String title;
    private String tutorName;
    private String thumbnailUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lectureStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lectureEnd;
    private String visibility;
    private String lectureStatus;
    private int price;
    // 수강률 추가
    private double progressPercent;
    private Long likeCount;



    // 변환 메서드
    public static MypageReservationDto of(Reservation r, Lecture l, double progressPercent) {
        LocalDateTime createdAt = null;
        Date createdAtDate = r.getCreatedAt();
        if (createdAtDate != null) {
            createdAt = createdAtDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        return MypageReservationDto.builder()
                .reservationId(r.getReservationId())
                .email(r.getUser().getEmail())
                .lectureId(l.getLectureId())   //  Lecture에서 가져오기
                .createdAt(createdAt)
                .status(r.getStatus().name())
                .lectureStart(l.getLectureStart())
                .lectureEnd(l.getLectureEnd())
                .visibility(l.getVisibility() != null ? l.getVisibility().name() : "ACTIVE")
                .lectureStatus(l.getStatus() != null ? l.getStatus().name() : "OPEN")
                .price(l.getPrice())
                .title(l.getTitle())           //  Lecture에서 가져오기
                .tutorName(l.getTutorName())
                .thumbnailUrl(l.getThumbnailUrl())
                .progressPercent(progressPercent)
                .build();
    }


    // JPA의 JPQL new() 구문이 사용하는 생성자
    public MypageReservationDto(
            Integer reservationId,
            Integer lectureId,
            String title,
            String tutorName,
            String thumbnailUrl,
            LocalDate lectureStart,
            LocalDate lectureEnd,
            Lecture.LectureVisibility visibility,
            Lecture.LectureStatus lectureStatus,
            int price,
            Double progressPercent,
            Long likeCount
    ) {
        this.reservationId = reservationId;
        this.lectureId = lectureId;
        this.title = title;
        this.tutorName = tutorName;
        this.thumbnailUrl = thumbnailUrl;
        this.lectureStart = lectureStart;
        this.lectureEnd = lectureEnd;
        this.visibility = visibility != null ? visibility.name() : null;
        this.lectureStatus = lectureStatus != null ? lectureStatus.name() : null;
        this.price = price;
        this.progressPercent = progressPercent != null ? progressPercent : 0.0;
        this.likeCount = likeCount != null ? likeCount : 0L;
    }

}
