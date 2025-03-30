package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Service;
import com.ar.nxg.nxgappts.repositories.CategoryServiceRepository;
import com.ar.nxg.nxgappts.repositories.ClientRepository;
import com.ar.nxg.nxgappts.repositories.ServiceRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/services")
public class ServiceController extends GlobalControllerAdvice {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CategoryServiceRepository categoryServiceRepository;

    @GetMapping(path = "/list")
    public String listarServicios(Model model, HttpSession httpSession) {
        model.addAttribute("services", serviceRepository.findByCompany((Company) httpSession.getAttribute("actualCompany")));
        String view = "services/list";
        attributesByMenu(model, view);
        return view;
    }

    @GetMapping(path = "/edit/{serviceId}")
    public String editService(Model model, @PathVariable(value = "serviceId") long serviceId) {
        Service service = serviceRepository.findById(serviceId).orElseThrow();
        model.addAttribute("service", service);
        model.addAttribute("categories", categoryServiceRepository.findAll());
        String view = "services/edit";
        attributesByMenu(model, view, joinManualBreadCrumbs(new String[]{'@'+service.getName()}));
        return view;
    }

    @GetMapping("/create")
    public String modalCreate(Model model) {
        model.addAttribute("categories", categoryServiceRepository.findAll());
        return "./services/modalCreate";
    }
}

