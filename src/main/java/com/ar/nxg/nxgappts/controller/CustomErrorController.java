package com.ar.nxg.nxgappts.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode != null) {
            if (statusCode == 404) {
                return "error-404"; // un template específico si querés
            } else if (statusCode == 500) {
                return "error-500";
            }
            // otros códigos...
        }

        return "error"; // fallback a página genérica
    }
}

