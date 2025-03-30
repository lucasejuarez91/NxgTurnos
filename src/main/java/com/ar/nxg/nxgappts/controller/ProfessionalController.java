package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Availability;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.repositories.AvailabilityRepository;
import com.ar.nxg.nxgappts.repositories.CompanyRepository;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import com.ar.nxg.nxgappts.repositories.ServiceRepository;
import com.ar.nxg.nxgappts.service.AvailabilityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/professionals")
public class ProfessionalController extends GlobalControllerAdvice {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    CompanyRepository companyRepository;

    @GetMapping(path = "/list")
    public String listProfessionals(Model model, HttpSession httpSession) {
        model.addAttribute("professionals", professionalRepository.findAllByCompany((Company) model.getAttribute("actualCompany")));
        String view = "professionals/list";
        attributesByMenu(model, view);
        return view;
    }

    @GetMapping("/create")
    public String modalCreate(Model model, HttpSession httpSession) {
        model.addAttribute("specialities", serviceRepository.findByCompany((Company) model.getAttribute("actualCompany")));
        return "./professionals/modalCreate";
    }

    @GetMapping(path = "/edit/{professionalId}")
    public String editProfessionals(Model model, @PathVariable long professionalId) {
        Professional prof = professionalRepository.findById(professionalId).orElseThrow();
        model.addAttribute("professional", prof);
        model.addAttribute("specialities", serviceRepository.findAll());
        String view = "professionals/edit";
        attributesByMenu(model, view, joinManualBreadCrumbs(new String[]{"@"+prof.getFullname()}));
        return view;
    }

    @GetMapping(path = "/availability/{professionalId}")
    public String availabilityProfessionals(Model model, @PathVariable long professionalId) {
        Professional prof = professionalRepository.findById(professionalId).orElseThrow();
        model.addAttribute("professional", prof);
        model.addAttribute("availabilities", availabilityRepository.findByProfessional(prof));
        String view = "professionals/availability";
        attributesByMenu(model, view, joinManualBreadCrumbs(new String[]{"@"+prof.getFullname()}));
        return view;
    }

    @PostMapping("/availability/save")
    public String saveAvailability(
            @RequestParam String profesionalId,
            @RequestParam String dayOfWeek,
            @RequestParam String startTime,
            @RequestParam String endTime
    ) {
        // Lógica para crear y asociar la disponibilidad al profesional
        Availability availability = new Availability();
        long profesionalIdLong = Long.parseLong(profesionalId);
        availability.setProfessional(professionalRepository.findById(profesionalIdLong).orElseThrow());
        availability.setDayOfWeek(DayOfWeek.valueOf(dayOfWeek.toUpperCase()));
        availability.setStartTime(LocalTime.parse(startTime));
        availability.setEndTime(LocalTime.parse(endTime));
        availabilityRepository.save(availability);

        return "redirect:./" + profesionalId; // Redirige a la lista de disponibilidades
    }

    @ResponseBody
    @GetMapping("/availability/appts")
    public List<Map<String, Object>> getAppts(@RequestParam("start") String startStr,
                                              @RequestParam("end") String endStr,
                                              @RequestParam("professionalId") Long professionalId, Locale locale) {
        LocalDate startDate = LocalDate.parse(startStr.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endStr.substring(0, 10));

        return availabilityService.getAvailabilityEvents(professionalId, startDate, endDate, locale);
    }

    @ResponseBody
    @GetMapping("/configCalendar")
    public Map<String, String> getConfig(HttpSession httpSession) {
        Company company = actualCompany(httpSession);
        Map<String, String> config = new HashMap<>();
        config.put("startTime", String.valueOf(company.getMinStartTime().plusHours(-1)));
        config.put("endTime", String.valueOf(company.getMaxEndtime().plusHours(1)));
        return config;
    }
}
