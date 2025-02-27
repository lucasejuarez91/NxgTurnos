package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.repositories.ClientRepository;
import com.ar.nxg.nxgappts.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CompanyController extends GlobalControllerAdvice {

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping(path = "/edit/{companyId}")
    public String listarClientes(Model model, @PathVariable(value = "companyId") long companyId) {
        model.addAttribute("company", companyRepository.findById(companyId).orElseThrow());
        attributesByMenu(model, 2);
        return "company/edit";
    }

    @GetMapping("/create")
    public String modalCreate(Model model) {
        return "./clients/modalCreate";
    }
}

