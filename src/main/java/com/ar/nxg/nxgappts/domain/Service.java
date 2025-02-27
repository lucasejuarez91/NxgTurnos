package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Service extends BaseEntity {

    private String name;
    private String description;
    private Double price;
    private Integer duration; // En minutos
    @ManyToOne
    @JoinColumn(name = "category_service_id")
    private CategoryService categoryService;
}
