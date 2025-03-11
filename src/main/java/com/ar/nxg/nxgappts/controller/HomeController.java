package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Role;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import com.ar.nxg.nxgappts.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController extends GlobalControllerAdvice {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MenuService menuService;

    @GetMapping("/")
    public ModelAndView showHome() {
        return new ModelAndView("home");
    }


}
