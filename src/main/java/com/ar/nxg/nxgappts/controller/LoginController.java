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
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class LoginController extends GlobalControllerAdvice {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MenuService menuService;

    @GetMapping("/login")
    public ModelAndView showLogin() {
        ModelAndView m = new ModelAndView("login");
        m.addObject("gitVersion", 1);
        return m;
    }

    @ModelAttribute("username")
    public String username(@AuthenticationPrincipal User user) { // Reemplaza User con tu clase
        return user != null ? user.getUsername() : "Invitado";
    }

    @ModelAttribute("avatarPath")
    public String avatarPath(@AuthenticationPrincipal User user) {
        return user != null && user.getAvatar() != null ? user.getAvatar().getName() : "/images/default-avatar.jpg";
    }

    @ModelAttribute
    public void addUserToModel(Model model) {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            LoggedUserDTO loggedUser = null;
            boolean isAdmin = false;
            if (user != null) {
                loggedUser = new LoggedUserDTO(user.getId(), user.getUsername(),
                        user.getAvatar() != null ? user.getAvatar().getName() : "", user.getEmail(),
                        user.getFullname(), user.getCompanies());
                // Agregar el objeto `principal` al modelo (puede ser un objeto `User` u otro,
                // según tu implementación)
                model.addAttribute("loggedUser", loggedUser);
                isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMINISTRATOR"));
            }
            model.addAttribute("isAdmin", isAdmin);
        }
    }

    @ModelAttribute
    public void addMenus(Model model) {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String menuItems = "";
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if (user != null) {
                // Obtener los roles del usuario (esto depende de tu implementación de autenticación)
                List<Role> roles = user.getRoles();
                // Obtener el menú basado en los roles del usuario
                menuItems = menuService.getMenuForUser(roles);
            }
        }
        model.addAttribute("menuItems", menuItems);
    }
}
