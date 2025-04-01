package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.*;
import com.ar.nxg.nxgappts.dto.AppointmentDTO;
import com.ar.nxg.nxgappts.dto.BookingDTO;
import com.ar.nxg.nxgappts.dto.BookingRequestDTO;
import com.ar.nxg.nxgappts.dto.ResponseMessage;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.ar.nxg.nxgappts.repositories.*;
import com.ar.nxg.nxgappts.service.*;
import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Null;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.LogManager;

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
    private MailService emailService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private EmailQueueService emailQueueService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ItemRepository itemRepository;

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
        double calificacion = 4.5;
        int totalRatings = 6345;

        model.addAttribute("calificacion", calificacion);
        model.addAttribute("calificacionInt", (int) calificacion); // Parte entera
        model.addAttribute("totalRatings", totalRatings);
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
        model.addAttribute("startTime", String.valueOf(company.getMinStartTime().minusHours(1)));
        model.addAttribute("endTime", String.valueOf(company.getMaxEndtime().plusHours(1)));
        return "./public/booking/select-date";
    }

    @ResponseBody
    @GetMapping("/request/slots/appts")
    public Map<String, Object> getAppts(@RequestParam("start") String startStr,
                                              @RequestParam("end") String endStr,
                                              @RequestParam("professionalId") Long professionalId, Locale locale) {
        LocalDate startDate = LocalDate.parse(startStr.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endStr.substring(0, 10));
        List<Map<String, Object>> events = availabilityService.getAvailabilityEvents(professionalId, startDate, endDate, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("isLoggedIn", getUserIdLogged() != null); // Booleano que indica si está logueado
        response.put("events", events); // Lista de eventos generados
        return response;
    }

    @ResponseBody
    @GetMapping("/request/slots/allAppts")
    public Map<String, Object> getAllAppts(HttpSession httpSession, Locale locale) {
        Company company = companyRepository.findById(actualCompany(httpSession).getId()).orElseThrow();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        List<Map<String, Object>> events = availabilityService.getAllEvents(startDate, endDate, company, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("isLoggedIn", getUserIdLogged() != null); // Booleano que indica si está logueado
        response.put("events", events); // Lista de eventos generados
        return response;
    }

    @PostMapping("/preconfirm")
    public ResponseEntity<Map<String, String>> getPreConfirm(@RequestBody Map data,
                                                   @ModelAttribute("booking") BookingDTO booking, Model model) throws MessagingException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME; // Usa el formato ISO 8601

        LocalDateTime startDate = LocalDateTime.parse((String) data.get("start"), formatter);
        LocalDateTime endDate = data.get("end") != null ? LocalDateTime.parse((String) data.get("end"), formatter) : null;

        booking.setStartDateTime(startDate);
        booking.setEndDateTime(endDate);
        ResponseMessage resp = new ResponseMessage();
        Appointment appt = new Appointment();
        appt.setApptStatus(AppointmentStatusEnum.CREATED);
        appt.setClient(getUserIdLogged());
        appt.setProfessional(professionalRepository.findById(booking.getProfessionalId()).orElseThrow());
        appt.setService(serviceRepository.findById(booking.getServiceId()).orElseThrow());
        appt.setScheduledDateStart(startDate);
        appt.setScheduledDateEnd(endDate);
        appt.setCompany(companyRepository.findById(booking.getSalonId()).orElseThrow());
        appt.setCode(appointmentService.generateBookingCode());
        appointmentRepository.save(appt);
        // Devolver la URL como JSON
        Map<String, String> response = new HashMap<>();
        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/booking/confirmation")
                .queryParam("bookingId", appt.getCode())
                .toUriString();
        response.put("redirectUrl", redirectUrl);
        //sendConfirmationMail(appt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirmation")
    public String showConfirmation(@RequestParam String bookingId, Model model) throws MessagingException {
        Appointment appt = appointmentRepository.getAppointmentByCode(bookingId);
        if(getUserIdLogged() == null){
            return "home";
        }
        if(!appt.getClient().getId().equals(getUserIdLogged().getId())){
            return "./public/appointments/list";
        }
        model.addAttribute("appt", appt);
        // Convertir LocalDateTime a Date antes de agregarlo al modelo
        model.addAttribute("scheduledDateStart",
                Date.from(appt.getScheduledDateStart().atZone(ZoneId.systemDefault()).toInstant()));
        return "public/booking/confirmation";
    }

    @GetMapping("/previewBooking")
    public String previewBooking(@RequestParam String appointmentCode, @RequestParam @Nullable Boolean confirm, Model model){
        Appointment appt = appointmentRepository.getAppointmentByCode(appointmentCode);
        model.addAttribute("initializated", false);
        if(appt == null || getUserIdLogged() == null){
            //model.addAttribute("initializated", false);
            return "home";
        }
        model.addAttribute("appt", appt);
        return "public/booking/confirmation";

    }

    @PostMapping("/initBooking")
    public String initBooking(@RequestParam String appointmentCode, @RequestParam @Nullable Boolean confirm, Model model){
        Appointment appt = appointmentRepository.getAppointmentByCode(appointmentCode);
        model.addAttribute("initializated", false);
        if(appt == null || getUserIdLogged() == null){
            //model.addAttribute("initializated", false);
            return "login";
        }
        model.addAttribute("appt", appt);
        User userProfessional = userRepository.findById(getUserIdLogged().getId()).orElseThrow();
        if(userProfessional.getRoles().contains(roleRepository.findByName("APPT_INITIATOR"))
                && appt.getApptStatus() == AppointmentStatusEnum.CONFIRM
                && Boolean.TRUE.equals(confirm)){
            appt.setApptStatus(AppointmentStatusEnum.IN_PROGRESS);
            appointmentRepository.save(appt);
            model.addAttribute("initializated", true);
        }
        return "public/booking/confirmation";

    }

    @GetMapping("/completeBooking")
    public String completeBooking(@RequestParam String bookingId, Model model){
        Appointment appt = appointmentRepository.getAppointmentByCode(bookingId);
        model.addAttribute("initializated", false);
        if(appt == null || getUserIdLogged() == null){
            //model.addAttribute("initializated", false);
            return "appointments/list";
        }
        Payment payment = paymentRepository.findByAppointment(appt);
        model.addAttribute("appt", appt);
        model.addAttribute("payment", payment);
        model.addAttribute("items", itemRepository.findByCompany(appt.getCompany()));
        return "/booking/completeBooking";

    }

}

