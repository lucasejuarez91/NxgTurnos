package com.ar.nxg.nxgappts;


import com.ar.nxg.nxgappts.domain.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<User> {

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty(); // No autenticado
        }

        // Este es el cambio, obteniendo el principal sin transacción
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return Optional.of((User) principal); // Retorna el usuario autenticado
        }
        return Optional.empty();
    }
}

