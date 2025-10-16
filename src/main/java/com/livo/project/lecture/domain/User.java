package com.livo.project.lecture.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "LectureUser")
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User { //민영 테스트 위한 간단 버전!!

    @Id
    private Long id;  // PK, 나중에 AUTO_INCREMENT로 바꿔도 됨

    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phone;

    @Temporal(TemporalType.DATE)
    private Date birth;

    private String gender;

    @Column(name = "role_id")
    private int roleId;
}
