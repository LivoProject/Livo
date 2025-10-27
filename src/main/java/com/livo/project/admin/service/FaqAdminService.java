package com.livo.project.admin.service;

import com.livo.project.faq.domain.Faq;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FaqAdminService {
    Faq updateFaq(int id, String question, String answer);
    List<Faq> getFaqAll();
    Page<Faq> getFaqPage(Pageable pageable);
    List<Faq> getFaqTop5();
    void deleteFaq(long faqId);
    Faq editFaq(int faqId);
}
