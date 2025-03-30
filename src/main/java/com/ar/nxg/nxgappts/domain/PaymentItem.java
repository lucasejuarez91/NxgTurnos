package com.ar.nxg.nxgappts.domain;

import com.ar.nxg.nxgappts.enums.ItemStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "item", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int quantity; // Cantidad del ítem en la compra

    @Column(nullable = false)
    private BigDecimal totalPrice; // price * quantity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatusEnum itemStatusEnum; // Enum: PENDING, DELIVERED, CANCELLED

    @Column(nullable = false, length = 3)
    private String currency;
}


