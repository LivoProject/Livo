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
@Table(name="attachment")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attachmentId;

    private String fileName;
    private String fileUrl;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private int lectureId;

    // Lecture랑 연결
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lectureId", referencedColumnName = "lectureId", insertable = false, updatable = false)
    private Lecture lecture;
}
