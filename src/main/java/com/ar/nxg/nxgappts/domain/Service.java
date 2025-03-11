package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne
    private Company company;
    @ManyToMany(mappedBy = "services")
    private List<Professional> professionals = new ArrayList<>();
}
