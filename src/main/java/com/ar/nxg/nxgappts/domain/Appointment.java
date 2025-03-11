package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Appointment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private Professional professional;

    @ManyToOne
    @JoinColumn(name = "appointment_status_id")
    private AppointmentStatus apptStatus;

    private LocalDateTime scheduledDateStart; // Fecha y hora de la cita
    private LocalDateTime scheduledDateEnd; // Fecha y hora de la cita

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

}
