package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.BaseEntity;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.repositories.RepositoryLocator;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import jakarta.persistence.MappedSuperclass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@MappedSuperclass
public class BaseService<T> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RepositoryLocator repositoryLocator;

    public <T extends BaseEntity> T saveOrUpdate(T entity) {
        JpaRepository<T, Long> repository = repositoryLocator.getRepository((Class<T>) entity.getClass());

        if(entity.getId() != null){
            entity.setChanger(getUserIdLogged());
            entity.setUpdatedDate(new Date());
        } else {
            entity.setCreatedDate(new Date());
            User loggedUser = getUserIdLogged();
            if(loggedUser == null){
                loggedUser = userRepository.findByUsername("WEBFORM");
            }
            entity.setCreator(loggedUser);
        }
        return repository.save(entity);
    }

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
}
