package com.ar.nxg.nxgappts.domain;

import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Appointment extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private Professional professional;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatusEnum apptStatus;

    private LocalDateTime scheduledDateStart; // Fecha y hora de la cita
    private LocalDateTime scheduledDateEnd; // Fecha y hora de la cita

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "appointment")
    private List<Payment> payments;

    private LocalDateTime startedAt;
    private Long startedByUserId;

    public String getPaymentTitle(boolean prePayment){
        return String.format("%s - %s [%s]", prePayment ? "Reserva" : "", this.company.getName(), this.scheduledDateStart);
    }

}
