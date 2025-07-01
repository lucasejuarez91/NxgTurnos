package com.ar.nxg.nxgappts.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private static final Map<DayOfWeek, String> dayTranslations = new HashMap<>();

    static {
        dayTranslations.put(DayOfWeek.MONDAY.plus(-1), "Lunes");
        dayTranslations.put(DayOfWeek.TUESDAY.plus(-1), "Martes");
        dayTranslations.put(DayOfWeek.WEDNESDAY.plus(-1), "Miércoles");
        dayTranslations.put(DayOfWeek.THURSDAY.plus(-1), "Jueves");
        dayTranslations.put(DayOfWeek.FRIDAY.plus(-1), "Viernes");
        dayTranslations.put(DayOfWeek.SATURDAY.plus(-1), "Sábado");
        dayTranslations.put(DayOfWeek.SUNDAY.plus(-1), "Domingo");
    }

    public static String translate(DayOfWeek dayOfWeek) {
        return dayTranslations.get(dayOfWeek);
    }

}
