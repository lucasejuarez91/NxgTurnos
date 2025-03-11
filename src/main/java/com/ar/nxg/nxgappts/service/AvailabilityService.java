package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Availability;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.repositories.AppointmentRepository;
import com.ar.nxg.nxgappts.repositories.AvailabilityRepository;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Map<String, Object>> getAvailabilityEvents(Long professionalId, LocalDate start, LocalDate end) {
        List<Availability> availabilities = availabilityRepository.findByProfessionalId(professionalId);

        List<Map<String, Object>> events = new ArrayList<>();
        LocalDate today = LocalDate.now();
        // Iterar sobre cada disponibilidad
        for (Availability availability : availabilities) {
            LocalDate eventDate = today.with(TemporalAdjusters.nextOrSame(availability.getDayOfWeek()));
            LocalDateTime startDateTime = LocalDateTime.of(eventDate, availability.getStartTime());
            LocalDateTime endDateTime = LocalDateTime.of(eventDate, availability.getEndTime());

            Map<String, Object> event = new HashMap<>();
            event.put("start", startDateTime.toString());  // "2025-03-02T09:00:00"
            event.put("end", endDateTime.toString());      // "2025-03-02T10:00:00"
            event.put("title", "Disponible");

            events.add(event);
        }
        return events;
    }

    public List<Map<String, Object>> getAllEvents(LocalDate startDate, LocalDate endDate, Company company) {
        List<Professional> professionals = professionalRepository.findAllByCompany(company);
        List<Map<String, Object>> events = new ArrayList<>();

        for (Professional prof : professionals) {
            List<Availability> availabilities = availabilityRepository.findByProfessionalId(prof.getId());
            List<Appointment> appointments = appointmentRepository.findByProfessional(prof);

            // Iterar sobre el rango de fechas dado
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();

                for (Availability availability : availabilities) {
                    if (availability.getDayOfWeek().equals(currentDayOfWeek)) {
                        LocalDateTime startDateTime = LocalDateTime.of(currentDate, availability.getStartTime());
                        LocalDateTime endDateTime = LocalDateTime.of(currentDate, availability.getEndTime());

                        // Buscar si hay un turno en este horario
                        Optional<Appointment> optionalAppt = appointments.stream()
                                .filter(appt -> appt.getScheduledDateStart().equals(startDateTime) &&
                                        appt.getScheduledDateEnd().equals(endDateTime))
                                .findFirst();

                        Map<String, Object> event = new HashMap<>();
                        event.put("start", startDateTime.toString());
                        event.put("end", endDateTime.toString());

                        if (optionalAppt.isPresent()) {
                            event.put("title", "Turno [" + optionalAppt.get().getClient().getFullname() + "]");
                            event.put("isBusy", true);
                        } else {
                            event.put("title", "Disponible");
                            event.put("isBusy", false);
                        }

                        events.add(event);
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return events;
    }

}

