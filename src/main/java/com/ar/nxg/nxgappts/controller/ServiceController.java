package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.repositories.CategoryServiceRepository;
import com.ar.nxg.nxgappts.repositories.ClientRepository;
import com.ar.nxg.nxgappts.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/services")
public class ServiceController extends GlobalControllerAdvice {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CategoryServiceRepository categoryServiceRepository;

    @GetMapping(path = "/list")
    public String listarServicios(Model model) {
        model.addAttribute("services", serviceRepository.findAll());
        attributesByMenu(model, 2);
        return "services/list";
    }

    @GetMapping("/create")
    public String modalCreate(Model model) {
        model.addAttribute("categories", categoryServiceRepository.findAll());
        return "./services/modalCreate";
    }
}

