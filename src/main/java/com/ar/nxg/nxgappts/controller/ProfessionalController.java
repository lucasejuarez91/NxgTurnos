package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Availability;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.dto.ProfessionalAvailabilityDTO;
import com.ar.nxg.nxgappts.dto.ResponseMessage;
import com.ar.nxg.nxgappts.repositories.AvailabilityRepository;
import com.ar.nxg.nxgappts.repositories.CompanyRepository;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import com.ar.nxg.nxgappts.repositories.ServiceRepository;
import com.ar.nxg.nxgappts.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final ProfessionalService professionalService;

    private final AvailabilityService availabilityService;

    private final ServicesService servicesService;
    private FilesService filesService;

    public ProfessionalController(ProfessionalService professionalService, AvailabilityService availabilityService, ServicesService servicesService, FilesService filesService) {
        this.professionalService = professionalService;
        this.availabilityService = availabilityService;
        this.servicesService = servicesService;
        this.filesService = filesService;
    }

    @GetMapping(path = "/list")
    public String listProfessionals(Model model) {
        model.addAttribute("professionals", professionalService.findAllByCompany((Company) model.getAttribute("actualCompany")));
        String view = "professionals/list";
        attributesByMenu(model, view);
        return view;
    }

    @GetMapping("/create")
    public String modalCreate(Model model, HttpSession httpSession) {
        model.addAttribute("specialities", servicesService.findByCompany((Company) httpSession.getAttribute("actualCompany")));
        return "./professionals/modalCreate";
    }

    @GetMapping(path = "/edit/{professionalId}")
    public String editProfessionals(Model model, @PathVariable long professionalId) {
        Professional prof = professionalService.findById(professionalId);
        model.addAttribute("professional", prof);
        model.addAttribute("specialities", servicesService.findAll());
        String view = "professionals/edit";
        attributesByMenu(model, view, joinManualBreadCrumbs(new String[]{"@"+prof.getFullname()}));
        return view;
    }

    @GetMapping(path = "/availability/{professionalId}")
    public String availabilityProfessionals(Model model, @PathVariable long professionalId) {
        Professional prof = professionalService.findById(professionalId);
        model.addAttribute("professional", prof);
        model.addAttribute("availabilities", availabilityService.findByProfessional(prof));
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
        availability.setProfessional(professionalService.findById(profesionalIdLong));
        availability.setDayOfWeek(DayOfWeek.valueOf(dayOfWeek.toUpperCase()));
        availability.setStartTime(LocalTime.parse(startTime));
        availability.setEndTime(LocalTime.parse(endTime));
        availabilityService.saveOrUpdate(availability);
        return "redirect:./" + profesionalId; // Redirige a la lista de disponibilidades
    }

    @ResponseBody
    @GetMapping("/availability/appts")
    public List<Map<String, Object>> getAppts(@RequestParam("start") String startStr,
                                                      @RequestParam("end") String endStr,
                                                      @RequestParam("professionalId") Long professionalId, Locale locale,
                                              @RequestParam("includeUnavailable") boolean includeUnavailable) {
        LocalDate startDate = LocalDate.parse(startStr.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endStr.substring(0, 10));
        return availabilityService.getAvailabilityEvents(professionalId, -1, startDate, endDate, locale, includeUnavailable);
    }

    @ResponseBody
    @GetMapping("/configCalendar")
    public Map<String, String> getConfig(HttpSession httpSession) {
        Company company = companyRepository.findById(1L).orElseThrow(); //actualCompany(httpSession);
        Map<String, String> config = new HashMap<>();
        //config.put("startTime", String.valueOf(company.getMinStartTime().plusHours(-1)));
        //config.put("endTime", String.valueOf(company.getMaxEndtime().plusHours(1)));
        return config;
    }

    @PostMapping("/updateAvatar/{professionalId}")
    public ResponseEntity<ResponseMessage> updateAvatar(@RequestBody Long fileId, @PathVariable(name = "professionalId") Long professionalId) {
        ResponseMessage resp = new ResponseMessage();
        try {
            Professional professional = professionalService.findById(professionalId);
            professional.setImage(filesService.findById(fileId));
            professionalService.saveOrUpdate(professional);
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
}
