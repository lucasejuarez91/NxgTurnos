package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.repositories.ServiceRepository;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends BaseService<User> {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

