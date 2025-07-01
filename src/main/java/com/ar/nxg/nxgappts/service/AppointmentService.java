package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.dto.AppointmentDTO;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.ar.nxg.nxgappts.repositories.AppointmentRepository;
import com.ar.nxg.nxgappts.repositories.ClientRepository;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import com.ar.nxg.nxgappts.repositories.RoleRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AppointmentService extends BaseService<Appointment> {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private EmailQueueService emailQueueService;

    @Autowired
    private RoleRepository roleRepository;

    // Método para cambiar el estado de la cita
    public void updateAppointmentStatus(String code, String status) {
        Appointment appointment = appointmentRepository.getAppointmentByCode(code);
        appointment.setApptStatus(AppointmentStatusEnum.valueOf(status));
        saveOrUpdate(appointment);
    }

    // Método para asignar un profesional a la cita
    public void assignProfessionalToAppointment(Long appointmentId, Long professionalId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        appointment.setProfessional(professional);
        saveOrUpdate(appointment);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow();
    }

    public List<Appointment> getAllAppointments(HttpSession httpSession) {
        Company company = (Company) httpSession.getAttribute("actualCompany");
        return appointmentRepository.findByCompanyOrderByApptStatusDesc(company);
    }

    public String generateBookingCode() {
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        return "BKG" + randomString.toUpperCase();
    }

    public void sendConfirmationMail(Appointment appt) throws MessagingException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", appt.getClient().getFullname());
        params.put("app", "NXG TURNOS");
        params.put("have_social_media", "false");
        params.put("company_name", appt.getCompany().getName());
        params.put("appt", new AppointmentDTO(appt));
        params.put("logo", appt.getCompany().getLogo().getPath());
        params.put("urlApple", generateAppleCalendarLink(appt.getCompany().getName(),appt.getService().getItem().getName(), appt.getCompany().getAddress(),
                appt.getScheduledDateStart(), appt.getScheduledDateEnd()));
        params.put("urlGoogle", generateGoogleCalendarLink(appt.getCompany().getName(),appt.getService().getItem().getName(), appt.getCompany().getAddress(),
                appt.getScheduledDateStart(), appt.getScheduledDateEnd()));
        emailQueueService.queueEmail(appt.getClient().getEmail(), "Confirmación de Turno", "emails/booking_confirmation", params);
    }

    public static String generateAppleCalendarLink(String title, String description, String location, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String formattedStart = start.format(formatter);
        String formattedEnd = end.format(formatter);

        return "https://calendar.apple.com/?action=TEMPLATE" +
                "&text=" + URLEncoder.encode(title, StandardCharsets.UTF_8) +
                "&details=" + URLEncoder.encode(description, StandardCharsets.UTF_8) +
                "&location=" + URLEncoder.encode(location, StandardCharsets.UTF_8) +
                "&dates=" + formattedStart + "/" + formattedEnd;
    }

    public static String generateGoogleCalendarLink(String title, String description, String location, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String formattedStart = start.format(formatter);
        String formattedEnd = end.format(formatter);

        return "https://www.google.com/calendar/render?action=TEMPLATE" +
                "&text=" + URLEncoder.encode(title, StandardCharsets.UTF_8) +
                "&details=" + URLEncoder.encode(description, StandardCharsets.UTF_8) +
                "&location=" + URLEncoder.encode(location, StandardCharsets.UTF_8) +
                "&dates=" + formattedStart + "/" + formattedEnd;
    }

    // Otros métodos como cancelar cita, filtrar citas, etc.
    public void confirmAppointment(Appointment appt) {
        appt.setApptStatus(AppointmentStatusEnum.CONFIRM);
        saveOrUpdate(appt);
    }

    public void closeAppointment(Appointment appt) throws MessagingException {
        appt.setApptStatus(AppointmentStatusEnum.COMPLETED);
        saveOrUpdate(appt);
        emailQueueService.sendCompletedBookingMail(appt);
    }

    public Appointment getAppointmentByCode(String code) {
        return appointmentRepository.getAppointmentByCode(code);
    }

    public List<Appointment> findByCompanyOrderByApptStatusDesc(Company company) {
        return appointmentRepository.findByCompanyOrderByApptStatusDesc(company);
    }

    public List<Appointment> findByClient(User userIdLogged) {
        return appointmentRepository.findByClient(userIdLogged);
    }

    public void initAppointment(Appointment appt, User userProfessional, Model model, Boolean confirm) {
        if (appt.getApptStatus() != AppointmentStatusEnum.CONFIRM) {
            model.addAttribute("error", "El turno ya fue iniciado o está en estado inválido.");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = appt.getScheduledDateStart();
        if (now.isBefore(start.minusMinutes(15)) || now.isAfter(start.plusMinutes(30))) {
            model.addAttribute("error", "Solo se puede iniciar el turno dentro del rango permitido.");
            return;
        }

        if(userProfessional.getRoles().contains(roleRepository.findByName("APPT_INITIATOR"))
                && appt.getApptStatus() == AppointmentStatusEnum.CONFIRM
                && Boolean.TRUE.equals(confirm)){
            appt.setApptStatus(AppointmentStatusEnum.IN_PROGRESS);
            appt.setStartedAt(LocalDateTime.now());
            appt.setStartedByUserId(userProfessional.getId());
            appointmentRepository.save(appt);
            model.addAttribute("initializated", true);
            model.addAttribute("statusMessage", "El turno fue iniciado correctamente");
            model.addAttribute("statusType", "success"); // o "error"
        }
    }

    public void cancelAppointment(Appointment appt) {
        appt.setApptStatus(AppointmentStatusEnum.CANCELLED);
        saveOrUpdate(appt);
        emailQueueService.sendCancelledBookingMail(appt);
    }
}

