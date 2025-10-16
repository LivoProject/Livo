package com.livo.project.main.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "notice")
@Getter
@Setter
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int noticeId;

    private String title;
    private String content;

    private Date createdAt;
    private Date updatedAt;

    private int viewCount;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isPinned;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isVisible;


}
