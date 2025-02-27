package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
public class Availability extends BaseEntity {

    @ManyToOne
    private Professional professional;

    private DayOfWeek dayOfWeek; // Enum de lunes a domingo
    private LocalTime startTime;
    private LocalTime endTime;

    private static final Map<DayOfWeek, String> dayTranslations = new HashMap<>();

    static {
        dayTranslations.put(DayOfWeek.MONDAY, "Lunes");
        dayTranslations.put(DayOfWeek.TUESDAY, "Martes");
        dayTranslations.put(DayOfWeek.WEDNESDAY, "Miércoles");
        dayTranslations.put(DayOfWeek.THURSDAY, "Jueves");
        dayTranslations.put(DayOfWeek.FRIDAY, "Viernes");
        dayTranslations.put(DayOfWeek.SATURDAY, "Sábado");
        dayTranslations.put(DayOfWeek.SUNDAY, "Domingo");
    }

    public static String translate(DayOfWeek dayOfWeek) {
        return dayTranslations.get(dayOfWeek);
    }

}
