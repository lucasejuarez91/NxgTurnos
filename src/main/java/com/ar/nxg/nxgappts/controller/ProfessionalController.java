package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Availability;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.repositories.AvailabilityRepository;
import com.ar.nxg.nxgappts.repositories.CompanyRepository;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import com.ar.nxg.nxgappts.repositories.SpecialityRepository;
import com.ar.nxg.nxgappts.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/professionals")
public class ProfessionalController extends GlobalControllerAdvice {

    @Value("${calendar.startTime:07:00}")
    private String calendarStartTime;

    @Value("${calendar.endTime:21:00}")
    private String calendarEndTime;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    CompanyRepository companyRepository;

    @GetMapping(path = "/list")
    public String listProfessionals(Model model) {
        model.addAttribute("professionals", professionalRepository.findAll());
        attributesByMenu(model, 4);
        return "professionals/list";
    }

    @GetMapping("/create")
    public String modalCreate(Model model) {
        model.addAttribute("specialities", specialityRepository.findAll());
        return "./professionals/modalCreate";
    }

    @GetMapping(path = "/edit/{professionalId}")
    public String editProfessionals(Model model, @PathVariable long professionalId) {
        model.addAttribute("professional", professionalRepository.findById(professionalId).orElseThrow());
        model.addAttribute("specialities", specialityRepository.findAll());
        attributesByMenu(model, 4);
        return "professionals/edit";
    }

    @GetMapping(path = "/availability/{professionalId}")
    public String availabilityProfessionals(Model model, @PathVariable long professionalId) {
        Professional prof = professionalRepository.findById(professionalId).orElseThrow();
        model.addAttribute("professional", prof);
        model.addAttribute("availabilities", availabilityRepository.findByProfessional(prof));
        attributesByMenu(model, 4);
        return "professionals/availability";
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
                                              @RequestParam("professionalId") Long professionalId) {
        LocalDate startDate = LocalDate.parse(startStr.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endStr.substring(0, 10));

        return availabilityService.getAvailabilityEvents(professionalId, startDate, endDate);
    }

    @ResponseBody
    @GetMapping("/configCalendar")
    public Map<String, String> getConfig(@RequestParam Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        Map<String, String> config = new HashMap<>();
        config.put("startTime", String.valueOf(company.getMinStartTime().plusHours(-1)));
        config.put("endTime", String.valueOf(company.getMaxEndtime().plusHours(1)));
        return config;
    }
}
