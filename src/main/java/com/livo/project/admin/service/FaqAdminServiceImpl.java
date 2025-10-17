package com.livo.project.admin.service;

import com.livo.project.faq.domain.Faq;
import com.livo.project.faq.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FaqAdminServiceImpl implements FaqAdminService {
    private final FaqRepository faqRepository;

    @Override
    public List<Faq> getFaqAll() {
        return faqRepository.findAll();
    }

    @Override
    public Page<Faq> getFaqPage(Pageable pageable) {
        return faqRepository.findAll(pageable);
    }
}
