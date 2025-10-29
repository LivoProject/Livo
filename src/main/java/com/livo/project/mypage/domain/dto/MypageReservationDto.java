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
    private String visibility;
    private String lectureStatus;
    // 수강률 추가
    private double progressPercent;

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
                .lectureId(l.getLectureId())   // ✅ Lecture에서 가져오기
                .createdAt(createdAt)
                .status(r.getStatus().name())
                .visibility(l.getVisibility() != null ? l.getVisibility().name() : "ACTIVE")
                .lectureStatus(l.getStatus() != null ? l.getStatus().name() : "OPEN")
                .title(l.getTitle())           // ✅ Lecture에서 가져오기
                .tutorName(l.getTutorName())
                .thumbnailUrl(l.getThumbnailUrl())
                .progressPercent(progressPercent)
                .build();
    }

}
