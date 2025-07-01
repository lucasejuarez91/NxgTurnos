package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class OpeningHour extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne
    private Company company;

    // Opcional: para soportar múltiples bloques en un mismo día (ej: mañana y tarde)
    private Integer blockOrder; // 0 = primer bloque, 1 = segundo, etc.

    @Override
    public String toString() {
        return "OpeningHour{" +
                "dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}

