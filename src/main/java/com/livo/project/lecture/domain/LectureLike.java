package com.livo.project.lecture.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name="lecture_like")
public class LectureLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int likeId;
    private int lectureId;
    private String email;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Lecture 연결
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lectureId", referencedColumnName = "lectureId", insertable = false, updatable = false)
    private Lecture lecture;

    // User 연결
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private User user;

}
