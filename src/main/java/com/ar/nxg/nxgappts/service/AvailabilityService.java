package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.*;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.ar.nxg.nxgappts.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AvailabilityService extends BaseService<Availability> {

    private final AvailabilityRepository availabilityRepository;
    private final ProfessionalRepository professionalRepository;
    private final AppointmentRepository appointmentRepository;
    private final MessageSource messageSource;
    private final UnavailableRepository unavailableRepository;
    private final ServicesService servicesService;

    public AvailabilityService(AvailabilityRepository availabilityRepository, ProfessionalRepository professionalRepository,
                               AppointmentRepository appointmentRepository, MessageSource messageSource, UnavailableRepository unavailableRepository, ServicesService servicesService) {
        this.availabilityRepository = availabilityRepository;
        this.professionalRepository = professionalRepository;
        this.appointmentRepository = appointmentRepository;
        this.messageSource = messageSource;
        this.unavailableRepository = unavailableRepository;
        this.servicesService = servicesService;
    }

    public List<Map<String, Object>> getAvailabilityEvents(Long professionalId, long serviceId, LocalDate start, LocalDate end, Locale locale, boolean includeUnavailable) {
        Professional professional = professionalRepository.findById(professionalId).orElseThrow();
        List<Availability> availabilities = availabilityRepository.findByProfessionalId(professionalId);
        validateAvailabilityMatchesOpeningHours(professional, availabilities);
        List<Appointment> appointments = appointmentRepository.findByProfessional(availabilities.get(0).getProfessional());
        long duration = 30;
        if(serviceId != -1){
            com.ar.nxg.nxgappts.domain.Service itemService = servicesService.findById(serviceId);
            duration = itemService.getDuration();
        }
        List<Map<String, Object>> events = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(30);
        // Convertimos LocalDate a LocalDateTime para buscar ausencias exactas
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.of(23, 59, 59));
        List<Unavailability> unavailabilities = unavailableRepository.findByProfessionalIdAndDateTimeRange(
                professional.getId(), startDateTime, endDateTime
        );
        System.out.println("Company opening hours:");
        professional.getCompany().getOpeningHours().forEach(oh ->
                System.out.println(oh.getDayOfWeek() + ": " + oh.getStartTime() + " - " + oh.getEndTime())
        );

        System.out.println("Disponibilidades del profesional:");
        availabilities.forEach(a ->
                System.out.println(a.getDayOfWeek() + ": " + a.getStartTime() + " - " + a.getEndTime())
        );


        // Verificamos si el profesional está marcado como no disponible este día
        if (includeUnavailable) {
            for (Unavailability unavailability : unavailabilities) {
                Map<String, Object> unavailableEvent = new HashMap<>();

                unavailableEvent.put("start", unavailability.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                unavailableEvent.put("end", unavailability.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                unavailableEvent.put("title", messageSource.getMessage("not.available", null, locale));
                unavailableEvent.put("backgroundColor", "#f87171");

                events.add(unavailableEvent);
            }
        }

        LocalDateTime now = LocalDateTime.now();

        // Redondear hacia la próxima media hora
        int minute = now.getMinute();
        int nextHalfHourMinute = (minute < 30) ? 30 : 0;
        LocalDateTime minSlotTime = now.withMinute(nextHalfHourMinute).withSecond(0).withNano(0);
        if (minute >= 30) {
            minSlotTime = minSlotTime.plusHours(1);
        }

        for (LocalDate date = today; !date.isAfter(limitDate); date = date.plusDays(1)) {
            DayOfWeek currentDayOfWeek = date.getDayOfWeek();

            for (Availability availability : availabilities) {
                if (availability.getDayOfWeek() == currentDayOfWeek) {
                    if (availability.getStartTime().equals(availability.getEndTime())) {
                        continue; // no hay franja válida
                    }

                    LocalDateTime availabilityStart = LocalDateTime.of(date, availability.getStartTime());
                    LocalDateTime availabilityEnd = LocalDateTime.of(date, availability.getEndTime());

                    //List<LocalDateTime> slots = generateTimeSlots(availabilityStart, availabilityEnd);

                    LocalDateTime slotStart = availabilityStart;
                    if (duration <= 0) {
                        throw new IllegalArgumentException("La duración del servicio debe ser mayor a 0 minutos.");
                    }
                    while (!slotStart.plusMinutes(duration).isAfter(availabilityEnd)) {
                        LocalDateTime slotEnd = slotStart.plusMinutes(duration);
                        if (slotStart.isBefore(minSlotTime)) {
                            slotStart = slotStart.plusMinutes(duration);
                            continue;
                        }

                        LocalDate finalDate = date;
                        LocalDateTime finalSlotStart2 = slotStart;
                        boolean isWithinOpeningHours = professional.getCompany().getOpeningHours().stream().anyMatch(oh ->
                                oh.getDayOfWeek().equals(finalDate.getDayOfWeek()) &&
                                        !finalSlotStart2.toLocalTime().isBefore(oh.getStartTime()) &&
                                        !slotEnd.toLocalTime().isAfter(oh.getEndTime())
                        );
                        if(!isWithinOpeningHours){
                            System.out.println();
                            continue;
                        }

                        // Verificar si el slot está ocupado por un turno
                        LocalDateTime finalSlotStart = slotStart;
                        boolean isTaken = appointments.stream().anyMatch(appt -> {
                            if (appt.getApptStatus() == AppointmentStatusEnum.CANCELLED) return false;
                            return appt.getScheduledDateStart().isBefore(slotEnd) &&
                                    appt.getScheduledDateEnd().isAfter(finalSlotStart);
                        });

                        if(isTaken && includeUnavailable){
                            Map<String, Object> unavailableEvent = new HashMap<>();
                            unavailableEvent.put("start", slotStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            unavailableEvent.put("end", slotEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            unavailableEvent.put("title", messageSource.getMessage("busy", null, locale));
                            unavailableEvent.put("backgroundColor", "#ffa500");

                            events.add(unavailableEvent);
                        }

                        // Verificar si el slot se solapa con una Unavailability
                        LocalDateTime finalSlotStart1 = slotStart;
                        boolean isUnavailable = unavailabilities.stream().anyMatch(u ->
                                u.getStartDate().isBefore(slotEnd) && u.getEndDate().isAfter(finalSlotStart1)
                        );

                        if (!isTaken && (!isUnavailable || includeUnavailable)) {
                            Map<String, Object> event = new HashMap<>();
                            event.put("start", slotStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            event.put("end", slotEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            event.put("title", messageSource.getMessage("available", null, locale));
                            events.add(event);
                        }

                        // Avanzar al siguiente slot sin superposición
                        slotStart = slotEnd;
                    }
                }
            }
        }
        return events;
    }

    private void validateAvailabilityMatchesOpeningHours(Professional professional, List<Availability> availabilities) {
        Set<DayOfWeek> openingDays = Optional.ofNullable(professional.getCompany())
                .map(Company::getOpeningHours)
                .orElse(Collections.emptyList())
                .stream()
                .map(OpeningHour::getDayOfWeek)
                .collect(Collectors.toSet());

        Set<DayOfWeek> availabilityDays = availabilities
                .stream()
                .map(Availability::getDayOfWeek)
                .collect(Collectors.toSet());

        boolean hasMatchingDay = availabilityDays.stream().anyMatch(openingDays::contains);

        if (!hasMatchingDay) {
            //throw new IllegalStateException("El profesional no tiene disponibilidades en días en que la empresa está abierta.");
            log.warn("El profesional no tiene disponibilidades en días en que la empresa está abierta.");
        }
    }


    public List<LocalDateTime> generateTimeSlots(LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> slots = new ArrayList<>();
        while (start.plusMinutes(30).isBefore(end) || start.plusMinutes(30).equals(end)) {
            slots.add(start);
            start = start.plusMinutes(30);
        }
        return slots;
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
                            event.put("service", optionalAppt.get().getService().getItem().getName());
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

    public List<Availability> findByProfessional(Professional prof) {
        return availabilityRepository.findByProfessional(prof);
    }
}

