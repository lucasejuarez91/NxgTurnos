package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AppointmentStatus extends BaseEntity {

    private String name; // Por ejemplo: "Pendiente", "Confirmada", "Atendida", "Cancelada"
}

