package com.livo.project.lecture.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;

    private String categoryName;
    private int categoryLevel;
    private int categoryOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pcategoryID")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Category> children = new ArrayList<>();
}
