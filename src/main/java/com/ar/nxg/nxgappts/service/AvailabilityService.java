package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Availability;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.ar.nxg.nxgappts.repositories.AppointmentRepository;
import com.ar.nxg.nxgappts.repositories.AvailabilityRepository;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

    @Autowired
    private MessageSource messageSource;

    public List<Map<String, Object>> getAvailabilityEvents(Long professionalId, LocalDate start, LocalDate end, Locale locale) {
        List<Availability> availabilities = availabilityRepository.findByProfessionalId(professionalId);

        List<Map<String, Object>> events = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(30); // Generar hasta 30 días adelante

        // Iterar sobre los próximos 30 días
        for (LocalDate date = today; !date.isAfter(limitDate); date = date.plusDays(1)) {
            DayOfWeek currentDayOfWeek = date.getDayOfWeek();

            // Revisar si el día actual coincide con algún día de disponibilidad del profesional
            for (Availability availability : availabilities) {
                if (availability.getDayOfWeek() == currentDayOfWeek) {
                    LocalDateTime startDateTime = LocalDateTime.of(date, availability.getStartTime());
                    LocalDateTime endDateTime = LocalDateTime.of(date, availability.getEndTime());

                    Map<String, Object> event = new HashMap<>();
                    List<Appointment> appointments = appointmentRepository.findByProfessional(availabilities.get(0).getProfessional());
                    // Buscar si hay un turno en este horario
                    Optional<Appointment> optionalAppt = appointments.stream()
                            .filter(appt -> appt.getScheduledDateStart().equals(startDateTime) &&
                                    appt.getScheduledDateEnd().equals(endDateTime) && !(appt.getApptStatus() == AppointmentStatusEnum.CANCELLED))
                            .findFirst();
                    if(optionalAppt.isPresent()){
                        continue;
                    }
                    event.put("start", startDateTime.toString());  // "2025-03-02T09:00:00"
                    event.put("end", endDateTime.toString());      // "2025-03-02T10:00:00"
                    String localizedTitle = messageSource.getMessage("available", null, locale);
                    event.put("title", localizedTitle);

                    events.add(event);
                }
            }
        }
        return events;
    }

    public List<Map<String, Object>> getAllEvents(LocalDate startDate, LocalDate endDate, Company company, Locale locale) {
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
                                        appt.getScheduledDateEnd().equals(endDateTime) && !(appt.getApptStatus() == AppointmentStatusEnum.CANCELLED))
                                .findFirst();

                        Map<String, Object> event = new HashMap<>();
                        event.put("start", startDateTime.toString());
                        event.put("end", endDateTime.toString());
                        event.put("prof", availability.getProfessional().getFullname());
                        event.put("swalTemplate","#viewEvent");
                        boolean available = true;
                        if (optionalAppt.isPresent()) {
                            event.put("title", optionalAppt.get().getClient().getFullname());
                            event.put("service", optionalAppt.get().getService().getName());
                            event.put("isBusy", true);
                            available = false;
                            event.put("color", "red");
                        } else {
                            //event.put("title", String.format("[%s - %s] Disponible", availability.getStartTime(),availability.getEndTime()));
                            event.put("isBusy", false);
                            event.put("color", "green");
                        }
                        String localizedTitle = messageSource.getMessage(available ? "available" : "not.available", null, locale);
                        if(optionalAppt.isEmpty()){
                            event.put("title", String.format("[%s - %s] %s", availability.getStartTime(),availability.getEndTime(), localizedTitle));
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

