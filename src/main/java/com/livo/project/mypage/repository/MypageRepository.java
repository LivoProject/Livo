package com.livo.project.mypage.repository;

import com.livo.project.auth.repository.UserRepository;

public class MypageRepository {
    private UserRepository userRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }


}
