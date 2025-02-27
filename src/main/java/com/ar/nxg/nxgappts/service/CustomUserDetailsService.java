package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Role;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.ar.nxg.nxgappts.domain.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!user.getStatus()) {
            throw new UsernameNotFoundException("user.not.active");
        }
        Set<String> roles = Optional.ofNullable(user.getRoles())
                .orElse(new ArrayList<Role>())
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return User.withUsername(user.getUsername()).password(user.getPassword()).roles(roles.toArray(new String[0]))
                .build();
    }
}

