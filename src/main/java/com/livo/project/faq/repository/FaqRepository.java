package com.livo.project.faq.repository;

import com.livo.project.faq.domain.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, String> {
}
