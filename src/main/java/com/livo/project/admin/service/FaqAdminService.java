package com.livo.project.admin.service;

import com.livo.project.faq.domain.Faq;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FaqAdminService {
    List<Faq> getFaqAll();
    Page<Faq> getFaqPage(Pageable pageable);
    List<Faq> getFaqTop5();
}
