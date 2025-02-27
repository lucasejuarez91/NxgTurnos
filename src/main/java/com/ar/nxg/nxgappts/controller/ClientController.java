package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clients")
public class ClientController extends GlobalControllerAdvice {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping(path = "/list")
    public String listarClientes(Model model) {
        model.addAttribute("clients", clientRepository.findAllBy());
        attributesByMenu(model, 2);
        return "clients/list";
    }

    @GetMapping("/create")
    public String modalCreate(Model model) {
        return "./clients/modalCreate";
    }
}

