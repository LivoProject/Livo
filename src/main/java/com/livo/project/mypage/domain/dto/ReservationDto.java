package com.livo.project.mypage.domain.dto;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {

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

    // 수강률 추가
    private double progressPercent;

    // 변환 메서드
    public static ReservationDto of(Reservation r, Lecture l, double progressPercent) {

        LocalDateTime createdAt = null;
        Date createdAtDate = r.getCreatedAt();
        if (createdAtDate != null) {
            createdAt = createdAtDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        return ReservationDto.builder()
                .reservationId(r.getReservationId())
                .email(r.getUser().getEmail())
                .lectureId(r.getLectureId())
                .createdAt(createdAt)
                .status(r.getStatus().name())
                .title(l.getTitle())
                .tutorName(l.getTutorName())
                .thumbnailUrl(l.getThumbnailUrl())
                .progressPercent(progressPercent)
                .build();
    }
}
