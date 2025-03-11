package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.repositories.*;
import com.ar.nxg.nxgappts.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController extends GlobalControllerAdvice {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Listar todas las citas
    @GetMapping(value = "/list")
    public String listAppointments(Model model, HttpSession httpSession) {
        List<Appointment> appointments = appointmentService.getAllAppointments((Company) httpSession.getAttribute("actualCompany"));
        model.addAttribute("appointments", appointments);
        return "appointments/list";
    }

    @GetMapping("/create")
    public String modalCreate(Model model, HttpSession httpSession) {
        model.addAttribute("clients", userRepository.findAll());
        model.addAttribute("services", serviceRepository.findByCompany((Company) httpSession.getAttribute("actualCompany")));
        model.addAttribute("professionals", professionalRepository.findByCompanyAndStatus((Company) httpSession.getAttribute("actualCompany"), true));
        return "./appointments/modalCreate";
    }

    // Ver detalles de una cita
    @GetMapping("/edit/{id}")
    public String editAppointment(@PathVariable Long id, Model model, HttpSession httpSession) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        model.addAttribute("clients", userRepository.findAll());
        model.addAttribute("services", serviceRepository.findByCompany((Company) httpSession.getAttribute("actualCompany")));
        model.addAttribute("professionals", professionalRepository.findByCompanyAndStatus((Company) httpSession.getAttribute("actualCompany"), true));
        model.addAttribute("appointment", appointment);
        return "appointments/edit";
    }

    // Cambiar estado de una cita
    @PostMapping("/{id}/change-status")
    public String changeAppointmentStatus(@PathVariable Long id, @RequestParam Long statusId) {
        appointmentService.updateAppointmentStatus(id, statusId);
        return "redirect:/appointments";
    }

    // Asignar profesional a una cita
    @PostMapping("/{id}/assign-professional")
    public String assignProfessional(@PathVariable Long id, @RequestParam Long professionalId) {
        appointmentService.assignProfessionalToAppointment(id, professionalId);
        return "redirect:/my-appointments";
    }

    // Otros métodos para cancelar citas, filtrar por fecha, etc.
    @GetMapping(value = "/my-appointments")
    public String myAppointments(Model model) {
        List<Appointment> appointments = appointmentRepository.findByClient(getUserIdLogged());
        model.addAttribute("appointments", appointments);
        return "./public/appointments/list";
    }

    @GetMapping(value = "/allappointments")
    public String allAppointments(Model model, HttpSession httpSession) {
        List<Appointment> appointments = appointmentRepository.findByCompany(actualCompany(httpSession));
        model.addAttribute("appointments", appointments);

        return "./public/appointments/list";
    }
}


