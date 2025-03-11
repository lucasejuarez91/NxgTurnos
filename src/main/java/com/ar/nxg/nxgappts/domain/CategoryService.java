package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class CategoryService extends BaseEntity {

    private String name;
    @OneToMany(mappedBy = "categoryService")
    private List<Service> services;
    @ManyToOne
    private Company company;
}
