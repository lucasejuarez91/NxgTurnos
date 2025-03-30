package com.ar.nxg.nxgappts.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Menu")
public class MenuController extends GlobalControllerAdvice {

    @GetMapping("/chngToggleSidebar")
    public String changeToggleSidebar(HttpSession session){
        String sidebarStatus = (String) session.getAttribute("toggleSidebar");
        // Toggle the sidebar status
        String newStatus = (sidebarStatus != null && sidebarStatus.equalsIgnoreCase("toggle-sidebar")) ? "" : "toggle-sidebar";
        session.setAttribute("toggleSidebar", newStatus);

        // Devuelve el nuevo estado para que el frontend lo maneje si es necesario
        return newStatus;
    }



}
