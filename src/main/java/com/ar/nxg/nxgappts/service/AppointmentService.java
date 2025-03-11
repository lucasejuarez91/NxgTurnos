package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.AppointmentStatus;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.repositories.AppointmentRepository;
import com.ar.nxg.nxgappts.repositories.AppointmentStatusRepository;
import com.ar.nxg.nxgappts.repositories.ClientRepository;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentStatusRepository statusRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ClientRepository clientRepository;

    // Método para cambiar el estado de la cita
    public Appointment updateAppointmentStatus(Long appointmentId, Long statusId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        AppointmentStatus status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        appointment.setApptStatus(status);
        return appointmentRepository.save(appointment);
    }

    // Método para asignar un profesional a la cita
    public Appointment assignProfessionalToAppointment(Long appointmentId, Long professionalId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        appointment.setProfessional(professional);
        return appointmentRepository.save(appointment);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow();
    }

    public List<Appointment> getAllAppointments(Company company) {
        return appointmentRepository.findByCompany(company);
    }

    // Otros métodos como cancelar cita, filtrar citas, etc.
}

