package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Role;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import com.ar.nxg.nxgappts.service.MenuService;
import com.ar.nxg.nxgappts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;

@Controller
public class LoginController extends GlobalControllerAdvice {

    private final UserService userService;
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView showLogin(@RequestParam(value = "redirect", required = false) String redirect, Model model) {
        ModelAndView m = new ModelAndView("login");
        m.addObject("gitVersion", 1);
        model.addAttribute("redirectTo", redirect);
        return m;
    }

    @ModelAttribute("username")
    public String username() { // Reemplaza User con tu clase
        return getUserIdLogged() != null ? getUserIdLogged().getUsername() : "Invitado";
    }

    @ModelAttribute("avatarPath")
    public String avatarPath() {
        return getUserIdLogged() != null && getUserIdLogged().getAvatar() != null ? getUserIdLogged().getAvatar().getName() : "/images/default-avatar.jpg";
    }

    @ModelAttribute
    public void addUserToModel(Model model) {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            LoggedUserDTO loggedUser = null;
            boolean isAdmin = false;
            if (user != null) {
                loggedUser = new LoggedUserDTO(user.getId(), user.getUsername(),
                        user.getAvatar() != null ? user.getAvatar().getName() : "", user.getEmail(),
                        user.getFullname(), user.getCompanies());
                model.addAttribute("loggedUser", loggedUser);
                isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMINISTRATOR"));
            }
            model.addAttribute("isAdmin", isAdmin);
        }
    }
}
