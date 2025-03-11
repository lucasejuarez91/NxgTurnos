package com.ar.nxg.nxgappts.dto;

import java.time.LocalDateTime;

public class BookingRequestDTO {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // Getters y Setters
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }
}

