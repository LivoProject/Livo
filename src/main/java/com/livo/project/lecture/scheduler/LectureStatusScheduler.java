package com.livo.project.lecture.scheduler;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.tags.shaded.org.apache.xpath.operations.Bool;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LectureStatusScheduler {
    private final LectureRepository lectureRepository;
    /**
     * 매 시간마다 강의 상태 자동 갱신
     * - 예약 종료일이 지나면 CLOSED
     * - 강의 종료일이 지나면 ENDED
     * - 그 외는 OPEN
     */
    //@Scheduled(cron = "0 0 * * * *") //매시간 정각
    @Scheduled(cron = "0 * * * * *") // 1분마다
    @Transactional
    public void updateLectureStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Lecture> lectures = lectureRepository.findAll();
        for (Lecture lec : lectures) {
            //무료강의일때
            if (Boolean.TRUE.equals(lec.getIsFree())){
                lec.setStatus(Lecture.LectureStatus.OPEN);
                continue;
            }
            //유료강의일때
            LocalDateTime lectureStart = lec.getLectureStart() != null
                    ? lec.getLectureStart().atStartOfDay()
                    : null;

            LocalDateTime lectureEnd = lec.getLectureEnd() != null
                    ? lec.getLectureEnd().atTime(LocalTime.MAX)  // 하루의 마지막 시각
                    : null;

            LocalDateTime reservationStart = lec.getReservationStart();
            LocalDateTime reservationEnd = lec.getReservationEnd();

            //  1. 강의 종료
            if (lectureEnd != null && now.isAfter(lectureEnd)) {
                lec.setStatus(Lecture.LectureStatus.ENDED);
            }
            //  2. 예약 가능 (예약기간 중 && 강의 아직 안 시작)
            else if (reservationStart != null && reservationEnd != null
                    && now.isAfter(reservationStart)
                    && now.isBefore(reservationEnd)
                    && (lectureStart == null || now.isBefore(lectureStart))) {
                lec.setStatus(Lecture.LectureStatus.OPEN);
            }
            //  3. 예약 종료 but 강의 진행 전
            else if (reservationEnd != null && now.isAfter(reservationEnd)
                    && lectureStart != null && now.isBefore(lectureStart)) {
                lec.setStatus(Lecture.LectureStatus.CLOSED);
            }

        }
    }


}
