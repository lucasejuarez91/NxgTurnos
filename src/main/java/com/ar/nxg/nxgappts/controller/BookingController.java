package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.*;
import com.ar.nxg.nxgappts.dto.BookingDTO;
import com.ar.nxg.nxgappts.dto.BookingRequestDTO;
import com.ar.nxg.nxgappts.dto.ResponseMessage;
import com.ar.nxg.nxgappts.repositories.*;
import com.ar.nxg.nxgappts.service.AvailabilityService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/booking")
@SessionAttributes("booking")
public class BookingController extends GlobalControllerAdvice {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CategoryServiceRepository categoryServiceRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentStatusRepository appointmentStatusRepository;

    @ModelAttribute("booking")
    public BookingDTO booking() {
        return new BookingDTO();
    }

    @GetMapping(path = "/request/list-company")
    public String listCompanies(Model model, @RequestParam(value = "name") @Nullable String name) {
        List<Company> companies;
        if(name != null && !name.isEmpty()){
            companies = companyRepository.findByNameContains(name);
        } else {
            companies = companyRepository.findAll();
        }
        model.addAttribute("companies", companies);
        return "./public/booking/list";
    }

    @GetMapping(path ="/request/select-salon/{salonId}")
    public String selectSalon(@PathVariable Long salonId, @ModelAttribute("booking") BookingDTO booking, Model model) {
        booking.setSalonId(salonId);
        Company company = companyRepository.findById(salonId).orElseThrow();
        model.addAttribute("company", company);
        model.addAttribute("categoryServices", categoryServiceRepository.findByCompany(company));
        return "./public/booking/select-service";
    }

    @GetMapping("/request/select-service")
    public String selectService(@ModelAttribute("booking") BookingDTO booking) {
        return "./public/booking/select-service";
    }

    @GetMapping("/request/select-professional/{serviceId}")
    public String selectProfessional(@PathVariable Long serviceId, @ModelAttribute("booking") BookingDTO booking, Model model) {
        Company company = companyRepository.findById(booking.getSalonId()).orElseThrow();
        model.addAttribute("company", company);
        booking.setServiceId(serviceId);
        List<Professional> professionals = professionalRepository.findByCompanyAndServices(company, serviceRepository.findById(serviceId));
        model.addAttribute("professionals", professionals);
        Service service = serviceRepository.findById(booking.getServiceId()).orElseThrow();
        model.addAttribute("service", service);
        return "./public/booking/select-professional";
    }

    @GetMapping("/request/select-date/{professionalId}")
    public String selectDate(@PathVariable Long professionalId, @ModelAttribute("booking") BookingDTO booking, Model model) {
        booking.setProfessionalId(professionalId);
        Company company = companyRepository.findById(booking.getSalonId()).orElseThrow();
        model.addAttribute("company", company);
        Service service = serviceRepository.findById(booking.getServiceId()).orElseThrow();
        model.addAttribute("service", service);
        Professional professional = professionalRepository.findById(booking.getProfessionalId()).orElseThrow();
        model.addAttribute("professional", professional);
        model.addAttribute("startTime", String.valueOf(company.getMinStartTime().plusHours(1)));
        model.addAttribute("endTime", String.valueOf(company.getMaxEndtime().plusHours(1)));
        return "./public/booking/select-date";
    }

    @ResponseBody
    @GetMapping("/request/slots/appts")
    public List<Map<String, Object>> getAppts(@RequestParam("start") String startStr,
                                              @RequestParam("end") String endStr,
                                              @RequestParam("professionalId") Long professionalId) {
        LocalDate startDate = LocalDate.parse(startStr.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endStr.substring(0, 10));

        return availabilityService.getAvailabilityEvents(professionalId, startDate, endDate);
    }

    @PostMapping("/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseMessage> confirmBooking(@ModelAttribute("booking") BookingDTO booking,
                                 @RequestBody BookingRequestDTO bookingRequest,
                                 RedirectAttributes redirectAttributes, Model model) {
        ResponseMessage resp = new ResponseMessage();
        LocalDateTime fechaInicio = bookingRequest.getFechaInicio();
        LocalDateTime fechaFin = bookingRequest.getFechaFin();

        Appointment appt = new Appointment();
        appt.setApptStatus(appointmentStatusRepository.findByName("CREADO"));
        appt.setClient(getUserIdLogged());
        appt.setProfessional(professionalRepository.findById(booking.getProfessionalId()).orElseThrow());
        appt.setService(serviceRepository.findById(booking.getServiceId()).orElseThrow());
        appt.setScheduledDateStart(fechaInicio);
        appt.setScheduledDateEnd(fechaFin);
        appt.setCompany(companyRepository.findById(booking.getSalonId()).orElseThrow());
        appointmentRepository.save(appt);

        // Configurar la respuesta de éxito
        resp.setError(false);
        resp.setMessage("Turno confirmado correctamente");
        return ResponseEntity.ok(resp);
    }

}

