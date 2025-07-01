package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.dto.ResponseMessage;
import com.ar.nxg.nxgappts.repositories.FilesRepository;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import com.ar.nxg.nxgappts.service.FilesService;
import com.ar.nxg.nxgappts.service.MenuService;
import com.ar.nxg.nxgappts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class UserController extends GlobalControllerAdvice {

    private final UserService userService;

    private final FilesService filesService;

    public UserController(UserService userService, FilesService filesService) {
        this.userService = userService;
        this.filesService = filesService;
    }

    @GetMapping("/my-profile")
    public ModelAndView showHome() {
        ModelAndView m = new ModelAndView("user/profile");
        m.addObject("user", userService.findById(getUserIdLogged().getId()));
        return m;
    }

    @PostMapping("/updateAvatar")
    public ResponseEntity<ResponseMessage> updateAvatar(@RequestBody Long fileId) {
        ResponseMessage resp = new ResponseMessage();

        try {
            User user = userService.findById(getUserIdLogged().getId());
            user.setAvatar(filesService.findById(fileId));
            userService.saveOrUpdate(user);
            // Configurar la respuesta de éxito
            resp.setError(false);
            resp.setMessage("Actualizado correctamente");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.setError(true);
            resp.setMessage("No se pudo actualizar la entidad");
            // logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }

    }

}
