package com.livo.project.main.repository;

import com.livo.project.main.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByCategoryLevelOrderByCategoryOrderAsc(int categoryLevel);

}

