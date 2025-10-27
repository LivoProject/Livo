package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByCategoryLevelOrderByCategoryOrderAsc(int categoryLevel);
    List<Category> findByParentIsNull();
    List<Category> findByParent_CategoryId(int parentId);

    //민영추가
    Category findByCategoryName(String categoryName);
}

