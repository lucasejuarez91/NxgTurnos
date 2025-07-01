package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.MenuItem;
import com.ar.nxg.nxgappts.domain.Role;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.dto.MenuDTO;
import com.ar.nxg.nxgappts.dto.ResponseMessage;
import com.ar.nxg.nxgappts.repositories.CompanyRepository;
import com.ar.nxg.nxgappts.repositories.MenuItemRepository;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import com.ar.nxg.nxgappts.service.MenuService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.rmi.AccessException;
import java.util.*;

@ControllerAdvice
public class GlobalControllerAdvice {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    MenuItemRepository menuItemRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    private MenuService menuService;

    @ExceptionHandler(AccessException.class)
    public String handleAccessDeniedException(AccessException ex, Model model) {
        model.addAttribute("statusCode", HttpStatus.FORBIDDEN.value());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ModelAttribute("locals")
    public void locals(){
        Map<String, String> locals = new HashMap<>();
        locals.put("es", "English");
        locals.put("en", "Español");
    }

	@ModelAttribute
	public void addMenus(Model model) {
		// Obtener el usuario autenticado del contexto de seguridad
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<MenuDTO> menuItems = new ArrayList<>();
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

	@ModelAttribute("toggleSidebar")
	public String toggledSidebar(HttpSession session) { // Reemplaza User con tu clase
        String sidebarStatus = (String) session.getAttribute("toggleSidebar");
        if(sidebarStatus == null || getUserIdLogged() == null || getUserIdLogged().getCompanies().isEmpty()){
            return "toggle-sidebar";
        }
        return sidebarStatus.equalsIgnoreCase("toggle-sidebar") ? "" : "toggle-sidebar";//""toggle-sidebar";
	}

    @ModelAttribute("isAdmin")
    public void toggledSidebar(Model model, HttpSession httpSession) { // Reemplaza User con tu clase
        model.addAttribute("isAdmin", true);
        httpSession.setAttribute("isAdmin", true);
    }

    @ModelAttribute("actualLocale")
    public void toggledSidebar(Model model, Locale locale) { // Reemplaza User con tu clase
        model.addAttribute("locale", locale.toString());
    }


    @ModelAttribute("actualCompany")
    public Company actualCompany(HttpSession session) {
        Company company = (Company) session.getAttribute("actualCompany");
        session.setAttribute("actualCompany", company != null ? company : (getUserIdLogged() != null ?
                !getUserIdLogged().getCompanies().isEmpty() ? getUserIdLogged().getCompanies().get(0) : null : null));
        return company;
    }

    @ModelAttribute("myCompanies")
    public List myCompanies() { // Reemplaza User con tu clase
        return getUserIdLogged() != null ? getUserIdLogged().getCompanies() : new ArrayList();
    }

    public void attributesByMenu(Model model, String viewName, String concatenated){
        MenuItem menuItemEntity = menuItemRepository.findByUrl(viewName.replace("./",""));
        if(menuItemEntity != null){
            menuItemEntity.setBreadcrumb(menuItemEntity.getBreadcrumb() + concatenated);
            List<String> breds = menuItemEntity.getBreadcrumbList();
            model.addAttribute("actualBreadcrumb", breds);
            model.addAttribute("actualView", breds.isEmpty() ? "" : breds.get(breds.size() - 2));
        }
    }

    public void attributesByMenu(Model model, String viewName){
        MenuItem menuItemEntity = menuItemRepository.findByUrl(viewName.replace("./",""));
        if(menuItemEntity != null){
            List<String> breds = menuItemEntity.getBreadcrumbList();
            model.addAttribute("actualBreadcrumb", breds);
            model.addAttribute("actualView", breds.isEmpty() ? "" : breds.get(breds.size() - 2));
        }
    }

    public String joinManualBreadCrumbs(String[] concatenated){
        return ";" + String.join(";", concatenated);
    }

	/*@ModelAttribute("avatarPath")
	public String avatarPath(@AuthenticationPrincipal User user) {
		return user != null && user.getAvatar() != null ? user.getAvatar().getName() : "/images/default-avatar.jpg";
	}*/


    public User getUserIdLogged() {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if (user != null) {
                return user;
            }
        }
        return null;
    }

    public static ResponseEntity<ResponseMessage> buildMessageResponse(boolean error, String message){
        ResponseMessage resp = new ResponseMessage();
        resp.setError(error);
        resp.setMessage(message);
        return ResponseEntity.status(error ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK).body(resp);
    }

    @Setter
    @Getter
    public class LoggedUserDTO {

        private long id;
        private String username;
        private String email;
        private String avatarUrl;
        private String fullname;
        private List<Company> companies;

        public LoggedUserDTO(Long id, String username, String avatarUrl, String email, String fullname, List<Company> companies) {
            super();
            this.id = id;
            this.username = username;
            this.email = email;
            this.avatarUrl = avatarUrl;
            this.fullname = fullname.toUpperCase();
            this.companies = companies;
        }

        public LoggedUserDTO() {
            // TODO Auto-generated constructor stub
        }

    }


}
