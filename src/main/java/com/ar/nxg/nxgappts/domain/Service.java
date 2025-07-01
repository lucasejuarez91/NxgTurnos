package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "item", nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "category_service_id")
    private CategoryService categoryService;
    @ManyToOne
    private Company company;
    @ManyToMany(mappedBy = "services")
    private List<Professional> professionals = new ArrayList<>();
    private long duration;
}
