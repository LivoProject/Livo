package com.livo.project.admin.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopLectureDto {

    private int lectureId;      // 강의Id
    private String title;       // 강좌명
    private long total;         // 전체 예약 수
    private long confirmed;     // 확정(결제완료/수강확정)
    private long canceled;      // 취소
    private long pending;       // 대기
    private long expired;       // 만료
    private Double reservationRate;

    // 예약률 계산용 (편의 메서드)
    public double getReservationRate() {
        if (total == 0) return 0.0;
        return (double) confirmed / total * 100;
    }
}
