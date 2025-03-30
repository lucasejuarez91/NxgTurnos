package com.ar.nxg.nxgappts.domain;

import com.ar.nxg.nxgappts.enums.ItemCategoryEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {

    private String name;
    private String description;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "company", nullable = false) // Un item pertenece a una sola empresa
    private Company company;

    @Enumerated(EnumType.STRING)
    private ItemCategoryEnum category; // Tipo de ítem: RESERVA, PRODUCTO, SERVICIO

}





