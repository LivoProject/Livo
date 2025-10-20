package com.livo.project.faqPage.repository;

import com.livo.project.faq.domain.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqPageRepository extends JpaRepository<Faq, Long> {
}