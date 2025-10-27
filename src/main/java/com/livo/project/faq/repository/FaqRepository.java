package com.livo.project.faq.repository;

import com.livo.project.faq.domain.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findTop5ByOrderByCreatedAtDesc();
}
