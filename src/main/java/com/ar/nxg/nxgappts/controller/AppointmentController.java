package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.*;
import com.ar.nxg.nxgappts.dto.BookingDTO;
import com.ar.nxg.nxgappts.dto.CompanyWithOpeningSummary;
import com.ar.nxg.nxgappts.dto.OpeningSummary;
import com.ar.nxg.nxgappts.dto.QrAppointmentDTO;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.ar.nxg.nxgappts.enums.CurrencyEnum;
import com.ar.nxg.nxgappts.enums.ItemStatusEnum;
import com.ar.nxg.nxgappts.enums.PaymentStatusEnum;
import com.ar.nxg.nxgappts.repositories.PaymentItemRepository;
import com.ar.nxg.nxgappts.repositories.PaymentRepository;
import com.ar.nxg.nxgappts.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/appointments")
@SessionAttributes("booking")
public class AppointmentController extends GlobalControllerAdvice {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private final AppointmentService appointmentService;

    private final ServicesService serviceService;

    private final UserService userService;

    private final ProfessionalService professionalService;

    private final PaymentService paymentService;

    private final ItemService itemService;

    private final PaymentItemService paymentItemService;

    private final CompanyService companyService;
    private final CategoryServicesService categoryServicesService;
    private final AvailabilityService availabilityService;
    private final PaymentItemRepository paymentItemRepository;
    private final PaymentRepository paymentRepository;
    private final EmailQueueService emailQueueService;
    private final EncryptionService encryptionService;

    @ModelAttribute("booking")
    public BookingDTO booking() {
        return new BookingDTO();
    }

    public AppointmentController(AppointmentService appointmentService, ServicesService serviceService, UserService userService,
                                 ProfessionalService professionalService, PaymentService paymentService, ItemService itemService,
                                 PaymentItemService paymentItemService, CompanyService companyService, CategoryServicesService categoryServicesService, AvailabilityService availabilityService, PaymentItemRepository paymentItemRepository, PaymentRepository paymentRepository, EmailQueueService emailQueueService, EncryptionService encryptionService) {
        this.appointmentService = appointmentService;
        this.serviceService = serviceService;
        this.userService = userService;
        this.professionalService = professionalService;
        this.paymentService = paymentService;
        this.itemService = itemService;
        this.paymentItemService = paymentItemService;
        this.companyService = companyService;
        this.categoryServicesService = categoryServicesService;
        this.availabilityService = availabilityService;
        this.paymentItemRepository = paymentItemRepository;
        this.paymentRepository = paymentRepository;
        this.emailQueueService = emailQueueService;
        this.encryptionService = encryptionService;
    }

    /**
     *
     * @param model
     * @param httpSession
     * @return
     */
    @GetMapping(value = "/list")
    public String listAppointments(Model model, HttpSession httpSession, Locale locale) {
        Company company = actualCompany(httpSession);
        if(company == null){
            return "redirect:appointments/my-appointments";
        }
        model.addAttribute("appointments", appointmentService.getAllAppointments(httpSession));
        //model.addAttribute("startTime", String.valueOf(company.getMinStartTime().minusHours(1)));
        //model.addAttribute("endTime", String.valueOf(company.getMaxEndtime().plusHours(1)));
        //model.addAttribute("openingSummaries", summarizeOpeningHours(company.getOpeningHours(), locale));
        String view = "appointment/list";
        attributesByMenu(model, view);
        return view;
    }

    @GetMapping(value = "/my-appointments")
    public String myAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.findByClient(getUserIdLogged()));
        String view = "public/appointments/list";
        attributesByMenu(model, view);
        return view;
    }

    /**
     *
     * @param model
     * @param httpSession
     * @return
     */
    @GetMapping("/create")
    public String modalCreate(Model model, HttpSession httpSession) {
        model.addAttribute("clients", userService.findAll());
        model.addAttribute("services", serviceService.findByCompany((Company) httpSession.getAttribute("actualCompany")));
        model.addAttribute("professionals", professionalService.findByCompanyAndStatus(httpSession, true));
        return "./appointment/modalCreate";
    }

    /**
     *
     * @param code
     * @param model
     * @param httpSession
     * @return
     */
    @GetMapping("/edit/{code}")
    public String editAppointment(@PathVariable String code, Model model, HttpSession httpSession) {
        Appointment appointment = appointmentService.getAppointmentByCode(code);
        List<Service> services;
        List<Professional> professionals;
        if(getUserIdLogged().getCompanies().isEmpty()){
            services = serviceService.findByCompany(appointment.getCompany());
            professionals = professionalService.findAllByCompany(appointment.getCompany());
        } else {
            services = serviceService.findByCompany((Company) httpSession.getAttribute("actualCompany"));
            professionals = professionalService.findByCompanyAndStatus(httpSession, true);
        }
        model.addAttribute("clients", userService.findById(getUserIdLogged().getId()));
        model.addAttribute("services", services);
        model.addAttribute("professionals", professionals);
        model.addAttribute("appointment", appointment);
        String view = "appointment/edit";
        attributesByMenu(model, view);
        return view;
    }

    /**
     *
     * @param model
     * @param httpSession
     * @return
     */
    @GetMapping(value = "/allAppts")
    public String allAppointments(Model model, HttpSession httpSession) {
        List<Appointment> appointments;
        if(actualCompany(httpSession) != null){
            appointments = appointmentService.findByCompanyOrderByApptStatusDesc(actualCompany(httpSession));
        } else {
            appointments = appointmentService.findByClient(getUserIdLogged());
        }
        model.addAttribute("appointments", appointments);
        return "./public/appointments/list";
    }

    /**
     *
     * @param apptCode
     * @param model
     * @return
     * @throws MessagingException
     */
    @PostMapping("/closeAppointment")
    public String closeAppointment(@RequestParam String apptCode, Model model) throws MessagingException {
        Appointment appt = appointmentService.getAppointmentByCode(apptCode);
        if(appt != null){
            appointmentService.closeAppointment(appt);
            model.addAttribute("appt", appt);
            return "redirect:/booking/completeBooking?bookingId=" + appt.getCode();
        }
        return "redirect:/appointment/list";
    }

    @PostMapping("/cancelAppointment")
    public String cancelAppointment(@RequestParam String apptCode, Model model) {
        Appointment appt = appointmentService.getAppointmentByCode(apptCode);
        User userLogged = getUserIdLogged();
        if(appt != null && Objects.equals(appt.getClient().getId(), userLogged.getId())){
            appointmentService.cancelAppointment(appt);
            model.addAttribute("appt", appt);
            return "redirect:/booking/completeBooking?bookingId=" + appt.getCode();
        }
        return "redirect:/appointment/list";
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping("/refreshTotalizers")
    public String refreshTotalizers(Model model){
        Appointment appt = (Appointment) model.getAttribute("appt");
        paymentService.addTotalizersInModelByAppt(appt, model);
        return "/booking/bookingDashboard :: totalizers";
    }

    /**
     *
     * @param item
     * @param quantity
     * @param bookingId
     * @param price
     * @param model
     * @return
     */
    @PostMapping("/addItems")
    public String addItemsToBooking(@RequestParam long item,
                                    @RequestParam int quantity,
                                    @RequestParam String bookingId,
                                    @RequestParam double price, Model model){
        Appointment appt = appointmentService.getAppointmentByCode(bookingId);
        model.addAttribute("appt", appt);
        Payment payment = paymentService.findByOrCreate(appt, PaymentStatusEnum.PENDING);
        PaymentItem pItem = createPItem(quantity, item, price, payment);
        payment.getPaymentItems().add(pItem);
        paymentService.saveOrUpdate(payment);
        model.addAttribute("paymentItems", paymentService.getAllPendingItemsToPayByApptAndItemsStatus(appt, null));
        paymentService.addTotalizersInModelByAppt(appt, model);
        return "/booking/tableItemsByAppt :: tblItemsByAppt";
    }

    /**
     *
     * @param quantity
     * @param item
     * @param price
     * @param payment
     * @return
     */
    private PaymentItem createPItem(int quantity, long item, double price, Payment payment){
        PaymentItem paymentItem = new PaymentItem();
        paymentItem.setQuantity(quantity);
        paymentItem.setItem(itemService.findById(item));
        paymentItem.setItemStatusEnum(ItemStatusEnum.PENDING);
        paymentItem.setPrice(new BigDecimal(price));
        paymentItem.setTotalPrice(new BigDecimal(quantity * price));
        paymentItem.setCurrency(CurrencyEnum.ARS);
        paymentItem.setPayment(payment);
        paymentItemService.saveOrUpdate(paymentItem);
        return paymentItem;
    }

    /**
     *
     * @param bookingId
     * @param model
     * @return
     */
    @GetMapping("/completeAppointment")
    public String completeAppointment(@RequestParam String bookingId, Model model){
        Appointment appt = appointmentService.getAppointmentByCode(bookingId);
        model.addAttribute("initializated", false);
        if(appt == null || getUserIdLogged() == null){
            return "appointments/list";
        }
        model.addAttribute("appt", appt);
        model.addAttribute("paymentItems", paymentService.getAllPendingItemsToPayByApptAndItemsStatus(appt, null));
        paymentService.addTotalizersInModelByAppt(appt, model);
        model.addAttribute("items", itemService.findItemsByCompany(appt.getCompany()));
        return "/booking/completeBooking";

    }

    @GetMapping("/initAppointment")
    public String initAppointment(@RequestParam("code") String encryptedCode,
                                  @RequestParam(value = "confirm", required = false, defaultValue = "true") boolean confirm,
                                  Model model) throws Exception {

        String appointmentCode = encryptionService.decrypt(encryptedCode);
        Appointment appt = appointmentService.getAppointmentByCode(appointmentCode);

        model.addAttribute("initializated", false);

        if (appt == null || getUserIdLogged() == null) {
            return "redirect:/login?redirectTo=/initAppointment?code=" +
                    URLEncoder.encode(encryptedCode, StandardCharsets.UTF_8);
        }

        model.addAttribute("appt", appt);

        User userProfessional = userService.findById(getUserIdLogged().getId());
        appointmentService.initAppointment(appt, userProfessional, model, confirm);

        return "public/booking/confirmation";
    }


    /**
     *
     * @param appointmentCode
     * @param model
     * @return
     */
    @GetMapping("/previewAppointment")
    public String previewAppointment(@RequestParam String appointmentCode, Model model){
        Appointment appt = appointmentService.getAppointmentByCode(appointmentCode);
        model.addAttribute("initializated", false);
        if(appt == null || getUserIdLogged() == null){
            return "home";
        }
        model.addAttribute("appt", appt);
        return "public/booking/confirmation";
    }

    /**
     *
     * @param bookingId
     * @param model
     * @param httpSession
     * @return
     */
    @GetMapping("/confirmation")
    public String showConfirmation(@RequestParam String bookingId, Model model, HttpSession httpSession) {
        Appointment appt = appointmentService.getAppointmentByCode(bookingId);
        if(getUserIdLogged() == null){
            return "home";
        }
        boolean isAdmin = ((Boolean) httpSession.getAttribute("isAdmin"));
        boolean isUserCreator = appt.getClient().getId().equals(getUserIdLogged().getId());
        if(!isAdmin && !isUserCreator){
            return "./public/appointments/list";
        }
        model.addAttribute("appt", appt);
        // Convertir LocalDateTime a Date antes de agregarlo al modelo
        model.addAttribute("scheduledDateStart",
                Date.from(appt.getScheduledDateStart().atZone(ZoneId.systemDefault()).toInstant()));
        model.addAttribute("scheduledDateEnd",
                Date.from(appt.getScheduledDateEnd().atZone(ZoneId.systemDefault()).toInstant()));
        return "public/booking/confirmation";
    }

    /**
     *
     * @param data
     * @param booking
     * @return
     */
    @PostMapping("/preconfirm")
    public ResponseEntity<Map<String, String>> getPreConfirm(@RequestBody Map data,
                                                             @ModelAttribute("booking") BookingDTO booking) throws MessagingException {
        LocalDateTime startDate = LocalDateTime.parse((String) data.get("start"), formatter);
        LocalDateTime endDate = data.get("end") != null ? LocalDateTime.parse((String) data.get("end"), formatter) : null;

        booking.setStartDateTime(startDate);
        booking.setEndDateTime(endDate);
        Appointment appt = new Appointment();
        appt.setApptStatus(AppointmentStatusEnum.CREATED);
        appt.setClient(getUserIdLogged());
        appt.setProfessional(professionalService.findById(booking.getProfessionalId()));
        appt.setService(serviceService.findById(booking.getServiceId()));
        appt.setScheduledDateStart(startDate);
        appt.setScheduledDateEnd(endDate);
        appt.setCompany(companyService.findById(booking.getSalonId()));
        appt.setCode(appointmentService.generateBookingCode());
        appointmentService.saveOrUpdate(appt);
        Payment payment = paymentService.findByOrCreate(appt, PaymentStatusEnum.PENDING);
        PaymentItem paymentItem = new PaymentItem();
        paymentItem.setPayment(payment);
        Item itemToPItem = itemService.findByNameAndCompany("RESERVATION", appt.getCompany());
        paymentItem.setItem(itemToPItem);
        paymentItem.setQuantity(1);
        paymentItem.setCurrency(CurrencyEnum.ARS);
        paymentItem.setPrice(itemToPItem.getPrice());
        paymentItem.setTotalPrice(itemToPItem.getPrice());
        paymentItem.setItemStatusEnum(ItemStatusEnum.PENDING);
        paymentItemRepository.save(paymentItem);
        payment.setAmount(itemToPItem.getPrice());
        paymentRepository.save(payment);
        // Devolver la URL como JSON
        Map<String, String> response = new HashMap<>();
        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/appointments/confirmation")
                .queryParam("bookingId", appt.getCode())
                .toUriString();
        response.put("redirectUrl", redirectUrl);
        emailQueueService.sendCreatedBookingMail(appt);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * @param model
     * @param name
     * @return
     */
    @GetMapping(path = "/request/list-company")
    public String listCompanies(Model model, @RequestParam(value = "name") @Nullable String name, Locale locale) {
        List<Company> companies;
        if(name != null && !name.isEmpty()){
            companies = companyService.findByNameContains(name);
        } else {
            companies = companyService.findAllByStatus(true);
        }
        double calificacion = 4.5;
        int totalRatings = 6345;

        model.addAttribute("calificacion", calificacion);
        model.addAttribute("calificacionInt", (int) calificacion); // Parte entera
        model.addAttribute("totalRatings", totalRatings);
        model.addAttribute("companies", companies);
        List<CompanyWithOpeningSummary> companiesWithSummaries = companies.stream()
                .map(comp -> new CompanyWithOpeningSummary(comp, summarizeOpeningHours(comp.getOpeningHours(), locale)))
                .collect(Collectors.toList());

        model.addAttribute("companiesWithSummaries", companiesWithSummaries);
        return "./public/booking/list";
    }

    /**
     *
     * @param salonId
     * @param booking
     * @param model
     * @return
     */
    @GetMapping(path ="/request/select-salon/{salonId}")
    public String selectSalon(@PathVariable Long salonId, @ModelAttribute("booking") BookingDTO booking, Model model) {
        booking.setSalonId(salonId);
        Company company = companyService.findById(salonId);
        model.addAttribute("company", company);
        model.addAttribute("categoryServices", categoryServicesService.findByCompany(company));
        return "./public/booking/select-service";
    }

    /**
     *
     * @param booking
     * @return
     */
    @GetMapping("/request/select-service")
    public String selectService(@ModelAttribute("booking") BookingDTO booking) {
        return "./public/booking/select-service";
    }

    /**
     *
     * @param serviceId
     * @param booking
     * @param model
     * @return
     */
    @GetMapping("/request/select-professional/{serviceId}")
    public String selectProfessional(@PathVariable Long serviceId, @ModelAttribute("booking") BookingDTO booking, Model model) {
        Company company = companyService.findById(booking.getSalonId());
        model.addAttribute("company", company);
        booking.setServiceId(serviceId);
        Service serv = serviceService.findById(serviceId);
        List<Professional> professionals = professionalService.findByCompanyAndServices(company, serv);
        model.addAttribute("professionals", professionals);
        Service service = serviceService.findById(booking.getServiceId());
        model.addAttribute("service", service);
        return "./public/booking/select-professional";
    }

    /**
     *
     * @param professionalId
     * @param booking
     * @param model
     * @return
     */
    @GetMapping("/request/select-date/{professionalId}")
    public String selectDate(@PathVariable Long professionalId, @ModelAttribute("booking") BookingDTO booking, Model model, Locale locale) {
        booking.setProfessionalId(professionalId);
        Company company = companyService.findById(booking.getSalonId());
        model.addAttribute("company", company);
        Service service = serviceService.findById(booking.getServiceId());
        model.addAttribute("service", service);
        Professional professional = professionalService.findById(booking.getProfessionalId());
        model.addAttribute("professional", professional);
        //model.addAttribute("startTime", String.valueOf(company.getMinStartTime().minusHours(1)));
        //model.addAttribute("endTime", String.valueOf(company.getMaxEndtime().plusHours(1)));
        //model.addAttribute("openingSummaries", summarizeOpeningHours(company.getOpeningHours(), locale));
        return "./public/booking/select-date";
    }

    /**
     *
     * @param startStr
     * @param endStr
     * @param professionalId
     * @param locale
     * @return
     */
    @ResponseBody
    @GetMapping("/request/slots/appts")
    public Map<String, Object> getAppts(@RequestParam("start") String startStr,
                                        @RequestParam("end") String endStr,
                                        @RequestParam("professionalId") Long professionalId, Locale locale,
                                        @RequestParam("serviceId") Long serviceId,
                                        @RequestParam("includeUnavailable") boolean includeUnavailable) {
        LocalDate startDate = LocalDate.parse(startStr.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endStr.substring(0, 10));
        List<Map<String, Object>> events = availabilityService.getAvailabilityEvents(professionalId, serviceId, startDate, endDate, locale, includeUnavailable);
        Map<String, Object> response = new HashMap<>();
        response.put("isLoggedIn", getUserIdLogged() != null); // Booleano que indica si está logueado
        response.put("events", events); // Lista de eventos generados
        return response;
    }

    /**
     *
     * @param httpSession
     * @param locale
     * @return
     */
    @ResponseBody
    @GetMapping("/request/slots/allAppts")
    public Map<String, Object> getAllAppts(HttpSession httpSession, Locale locale) {
        Company company = companyService.findById(actualCompany(httpSession).getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        List<Map<String, Object>> events = availabilityService.getAllEvents(startDate, endDate, company, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("isLoggedIn", getUserIdLogged() != null); // Booleano que indica si está logueado
        response.put("events", events); // Lista de eventos generados
        return response;
    }

    public List<OpeningSummary> summarizeOpeningHours(List<OpeningHour> openingHours, Locale locale) {
        Map<Integer, List<OpeningHour>> groupedByBlock = openingHours.stream()
                .collect(Collectors.groupingBy(OpeningHour::getBlockOrder));

        List<OpeningSummary> summaries = new ArrayList<>();

        for (Map.Entry<Integer, List<OpeningHour>> entry : groupedByBlock.entrySet()) {
            List<OpeningHour> blockHours = entry.getValue();

            // Agrupar por día
            Map<DayOfWeek, List<OpeningHour>> groupedByDay = blockHours.stream()
                    .collect(Collectors.groupingBy(OpeningHour::getDayOfWeek));

            for (Map.Entry<DayOfWeek, List<OpeningHour>> dayEntry : groupedByDay.entrySet()) {
                DayOfWeek day = dayEntry.getKey();
                List<OpeningHour> hours = dayEntry.getValue();

                LocalTime minStart = hours.stream()
                        .map(OpeningHour::getStartTime)
                        .min(Comparator.naturalOrder())
                        .orElse(null);

                LocalTime maxEnd = hours.stream()
                        .map(OpeningHour::getEndTime)
                        .max(Comparator.naturalOrder())
                        .orElse(null);

                if (minStart != null && maxEnd != null) {
                    summaries.add(new OpeningSummary(day, day, minStart, maxEnd));
                }
            }
        }
        summaries.sort(Comparator.comparing(OpeningSummary::getFirstDay));
        return summaries;
    }

    @GetMapping("/{apptCode}/qr")
    public ResponseEntity<String> getQrData(@PathVariable String apptCode) {
        Appointment appt = appointmentService.getAppointmentByCode(apptCode);
        if (appt == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(encryptionService.encrypt(appt.getCode()));
    }
}


