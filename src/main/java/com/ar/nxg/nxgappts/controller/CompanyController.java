package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Service;
import com.ar.nxg.nxgappts.dto.BookingDTO;
import com.ar.nxg.nxgappts.dto.ResponseMessage;
import com.ar.nxg.nxgappts.repositories.*;
import com.ar.nxg.nxgappts.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/company")
public class CompanyController extends GlobalControllerAdvice {

    private final CompanyService companyService;

    private final CategoryServicesService categoryServicesService;

    private final AvailabilityService availabilityService;

    private final ServicesService servicesService;
    private final FilesService filesService;

    public CompanyController(CompanyService companyService, CategoryServicesService categoryServicesService, AvailabilityService availabilityService, ServicesService servicesService, FilesService filesService) {
        this.companyService = companyService;
        this.categoryServicesService = categoryServicesService;
        this.availabilityService = availabilityService;
        this.servicesService = servicesService;
        this.filesService = filesService;
    }

    @GetMapping(path = "/edit/{companyId}")
    @PreAuthorize("isAuthenticated()")
    public String listarClientes(Model model, @PathVariable(value = "companyId") long companyId, Locale locale) {
        Company company = companyService.findById(companyId);
        model.addAttribute("company", company);
        model.addAttribute("professionals", company.getProfessionals());
        model.addAttribute("days", getDaysList(locale));
        String view = "company/edit";
        attributesByMenu(model, view);
        return view;
    }

    private List getDaysList(Locale locale){
        List<String> days = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", locale); // "EEEE" para el nombre completo del día
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            cal.set(Calendar.DAY_OF_WEEK, i); // Establecemos el día de la semana
            days.add(sdf.format(cal.getTime())); // Obtenemos el nombre del día
        }
        return days;
    }

    @GetMapping(path = "/list")
    @PreAuthorize("isAuthenticated()")
    public String myCompanies(Model model) {
        model.addAttribute("company", getUserIdLogged().getCompanies());
        String view = "company/list";
        attributesByMenu(model, view);
        return view;
    }

    @PostMapping("/updateAvatar/{companyId}")
    public ResponseEntity<ResponseMessage> updateAvatar(@RequestBody Long fileId, @PathVariable(name = "companyId") Long companyId) {
        ResponseMessage resp = new ResponseMessage();
        try {
            Company existingCompany = companyService.findById(companyId);
            existingCompany.setLogo(filesService.findById(fileId));
            companyService.saveOrUpdate(existingCompany);
            // Configurar la respuesta de éxito
            resp.setError(false);
            resp.setMessage("Actualizado correctamente");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.setError(true);
            resp.setMessage("No se pudo actualizar la entidad");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }

    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String modalCreate() {
        return "./clients/modalCreate";
    }

    @GetMapping("/changeCompany/{companyId}")
    @PreAuthorize("isAuthenticated()")
    public String setActualCompany(@PathVariable long companyId, HttpSession session, HttpServletRequest httpServletRequest) {
        getUserIdLogged().getCompanies()
                .stream()
                .filter(company -> company.getId() == companyId)
                .findFirst().ifPresent(selectedCompany -> session.setAttribute("actualCompany", selectedCompany));
        String referer = httpServletRequest.getHeader("Referer");

        // Si el referer está presente, redirigir a esa URL; si no, redirigir a la página principal
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }

        // En caso de no tener un referer, redirigir a la página principal (o la vista que desees)
        return "redirect:/";
    }

    /* Public Site*/

    @ModelAttribute("booking")
    public BookingDTO booking() {
        return new BookingDTO();
    }
    @GetMapping(path = "/public/list")
    public String listCompanies(Model model) {
        model.addAttribute("companies", companyService.findAll());
        return "./public/companies/list";
    }

    @GetMapping("/select-company/{companyId}")
    public String selectSalon(@PathVariable Long companyId, @ModelAttribute("booking") BookingDTO booking) {
        booking.setSalonId(companyId);
        return "redirect:/booking/view";
    }

    @GetMapping(path = "/public/view/{companyId}")
    public String listCompanies(Model model, @PathVariable(value = "companyId") long companyId) {
        Company company = companyService.findById(companyId);
        model.addAttribute("company", company);
        model.addAttribute("categoryServices", categoryServicesService.findByCompany(company));
        return "./public/companies/view";
    }

    @GetMapping(path = "/public/view/{companyId}/{serviceId}")
    public String listCompanies(Model model, @PathVariable(value = "companyId") long companyId, @PathVariable(value = "serviceId") long serviceId) {
        Company company = companyService.findById(companyId);
        model.addAttribute("company", company);
        Service service = servicesService.findById(serviceId);
        model.addAttribute("company", company);
        model.addAttribute("service", service);
        model.addAttribute("categoryServices", categoryServicesService.findByCompany(company));
        return "./public/companies/view/service/view";
    }

    @ResponseBody
    @GetMapping("/availability/slots")
    public List<Map<String, Object>> getSlots(@RequestParam("start") String startStr,
                                              @RequestParam("end") String endStr, HttpSession httpSession, Locale locale) {
        LocalDate startDate = LocalDate.parse(startStr.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endStr.substring(0, 10));
        return availabilityService.getAllEvents(startDate, endDate, actualCompany(httpSession), locale);
    }
}

