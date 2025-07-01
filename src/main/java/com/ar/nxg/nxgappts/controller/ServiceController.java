package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Item;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.domain.Service;
import com.ar.nxg.nxgappts.dto.ResponseMessage;
import com.ar.nxg.nxgappts.repositories.CategoryServiceRepository;
import com.ar.nxg.nxgappts.repositories.ClientRepository;
import com.ar.nxg.nxgappts.repositories.ServiceRepository;
import com.ar.nxg.nxgappts.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/services")
public class ServiceController extends GlobalControllerAdvice {

    private final ServicesService servicesService;

    private final CategoryServicesService categoryServicesService;
    private final ItemService itemService;
    private final FilesService filesService;

    public ServiceController(ServicesService servicesService, CategoryServicesService categoryServicesService, ItemService itemService, FilesService filesService) {
        this.servicesService = servicesService;
        this.categoryServicesService = categoryServicesService;
        this.itemService = itemService;
        this.filesService = filesService;
    }

    @GetMapping(path = "/list")
    public String listarServicios(Model model, HttpSession httpSession) {
        model.addAttribute("services", servicesService.findByCompany((Company) httpSession.getAttribute("actualCompany")));
        String view = "services/list";
        attributesByMenu(model, view);
        return view;
    }

    @GetMapping(path = "/edit/{serviceId}")
    public String editService(Model model, @PathVariable(value = "serviceId") long serviceId) {
        Service service = servicesService.findById(serviceId);
        model.addAttribute("service", service);
        model.addAttribute("categories", categoryServicesService.findAll());
        String view = "services/edit";
        attributesByMenu(model, view, joinManualBreadCrumbs(new String[]{'@'+service.getItem().getName()}));
        return view;
    }

    @GetMapping("/create")
    public String modalCreate(Model model) {
        model.addAttribute("categories", categoryServicesService.findAll());
        return "./services/modalCreate";
    }

    @PostMapping("/updateAvatar/{itemId}")
    public ResponseEntity<ResponseMessage> updateAvatar(@RequestBody Long fileId, @PathVariable(name = "itemId") Long itemId) {
        ResponseMessage resp = new ResponseMessage();
        try {
            Item item = itemService.findById(itemId);
            item.setImage(filesService.findById(fileId));
            itemService.saveOrUpdate(item);
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

