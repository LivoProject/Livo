package com.livo.project.report.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.livo.project.lecture.domain.User;
import com.livo.project.review.domain.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;

    private String reportReason;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PROCESSING;

    private String email;   // FK (user)
    private int reviewUId;  // FK (review)

    // Review 연결
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewUId", referencedColumnName = "reviewUId", insertable = false, updatable = false)
    private Review review;

    // User 연결
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private User user;

    public enum Status {
        COMPLETED, PROCESSING, REJECT
    }
}
