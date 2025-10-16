package com.livo.project.lecture.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;

    private String categoryName;
    private int categoryLevel;
    private int categoryOrder;

    private Integer pcategoryID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcategoryID", insertable = false, updatable = false)
    private Category parentCategory;

}

