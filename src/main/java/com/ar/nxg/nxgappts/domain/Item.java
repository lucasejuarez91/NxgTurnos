package com.ar.nxg.nxgappts.domain;

import com.ar.nxg.nxgappts.enums.ItemCategoryEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal price;
    @OneToOne
    @JoinColumn(name = "image_item_file_id", referencedColumnName = "id", nullable = true)
    private Files image;

    @ManyToOne
    @JoinColumn(name = "company", nullable = false) // Un item pertenece a una sola empresa
    private Company company;

    @Enumerated(EnumType.STRING)
    private ItemCategoryEnum category; // Tipo de ítem: RESERVA, PRODUCTO, SERVICIO

}





