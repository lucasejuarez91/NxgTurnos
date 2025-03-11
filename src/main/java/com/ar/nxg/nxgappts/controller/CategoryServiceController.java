package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.CategoryService;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Service;
import com.ar.nxg.nxgappts.repositories.CategoryServiceRepository;
import com.ar.nxg.nxgappts.repositories.ServiceRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categoryService")
public class CategoryServiceController extends GlobalControllerAdvice {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CategoryServiceRepository categoryServiceRepository;

    @GetMapping(path = "/list")
    public String listarServicios(Model model) {
        model.addAttribute("services", serviceRepository.findAll());
        attributesByMenu(model, 2);
        return "categoryServices/list";
    }

    @GetMapping(path = "/edit/{categoryServiceId}")
    public String editService(Model model, @PathVariable(value = "categoryServiceId") long categoryServiceId, HttpSession httpSession) {
        Company sessionCompany = (Company) httpSession.getAttribute("actualCompany");
        CategoryService categoryService = categoryServiceRepository.findByIdAndCompany(categoryServiceId, sessionCompany);
        model.addAttribute("categoryService", categoryService);
        model.addAttribute("services", categoryService.getServices().stream().filter(service -> service.getCompany() == sessionCompany));
        attributesByMenu(model, 2);
        return "./categoryServices/edit";
    }

    @GetMapping("/create")
    public String modalCreate(Model model) {
        model.addAttribute("categories", categoryServiceRepository.findAll());
        return "./services/modalCreate";
    }
}

