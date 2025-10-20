package com.livo.project.faqPage.service;

import com.livo.project.faq.domain.Faq;
import com.livo.project.faqPage.repository.FaqPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FaqPageService {

    private final FaqPageRepository faqPageRepository;

    public List<Faq> findAllFaqPages() {
        return faqPageRepository.findAll();
    }
}