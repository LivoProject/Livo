package com.livo.project.admin.service;

import com.livo.project.faq.domain.Faq;
import com.livo.project.faq.repository.FaqRepository;
import com.livo.project.faqPage.repository.FaqPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class FaqAdminServiceImpl implements FaqAdminService {
    private final FaqRepository faqRepository;
    private final FaqPageRepository faqPageRepository;

    @Override
    public Faq updateFaq(int id, String question, String answer) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));
        faq.setQuestion(question);
        faq.setAnswer(answer);

        return faqRepository.save(faq);
    }

    @Override
    public List<Faq> getFaqAll() {
        return faqRepository.findAll();
    }

    @Override
    public Page<Faq> getFaqPage(Pageable pageable) {
        return faqRepository.findAll(pageable);
    }

    @Override
    public List<Faq> getFaqTop5() {
        return faqRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public void deleteFaq(long faqId) {
        faqPageRepository.deleteById(faqId);
    }

    @Override
    public Faq editFaq(int faqId) {
        return faqRepository.findById(faqId)
                .orElseThrow(() -> new IllegalArgumentException("faq를 찾을 수 없습니다."));
    }
}
