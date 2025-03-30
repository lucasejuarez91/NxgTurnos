package com.ar.nxg.nxgappts.domain;

import com.ar.nxg.nxgappts.enums.PaymentMethodEnum;
import com.ar.nxg.nxgappts.enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "payment_code", nullable = false, unique = true)
    private String paymentCode = generateRandomCode();

    @OneToOne
    @JoinColumn(name = "appointment", nullable = false, unique = true)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatusEnum paymentStatusEnum; // Enum: PENDING, APPROVED, REJECTED, CANCELLED

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethodEnum paymentMethodEnum; // Enum: CREDIT_CARD, DEBIT_CARD, MERCADOPAGO, PAYPAL

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency; // "ARS", "USD"

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentItem> paymentItems = new ArrayList<>();

    private String generateRandomCode(){
        return "PAY" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}



