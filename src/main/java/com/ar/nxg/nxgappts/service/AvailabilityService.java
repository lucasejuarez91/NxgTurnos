package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Availability;
import com.ar.nxg.nxgappts.repositories.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    public List<Map<String, Object>> getAvailabilityEvents(Long professionalId, LocalDate start, LocalDate end) {
        List<Availability> availabilities = availabilityRepository.findByProfessionalId(professionalId);

        List<Map<String, Object>> events = new ArrayList<>();

        // Iterar sobre cada disponibilidad
        for (Availability availability : availabilities) {
            LocalDate current = start;

            // Recorrer las fechas dentro del rango de la consulta
            while (!current.isAfter(end)) {
                if (current.getDayOfWeek() == availability.getDayOfWeek()) {
                    Map<String, Object> event = new HashMap<>();
                    event.put("title", "Disponible");
                    event.put("start", LocalDateTime.of(current, availability.getStartTime()).toString());
                    event.put("end", LocalDateTime.of(current, availability.getEndTime()).toString());
                    events.add(event);
                }
                current = current.plusDays(1);
            }
        }
        return events;
    }
}

