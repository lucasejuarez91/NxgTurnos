package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.dto.AppointmentDTO;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.ar.nxg.nxgappts.repositories.AppointmentRepository;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import com.ar.nxg.nxgappts.repositories.ServiceRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ServicesService extends BaseService<com.ar.nxg.nxgappts.domain.Service> {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<com.ar.nxg.nxgappts.domain.Service> findByCompany(Company company) {
        return serviceRepository.findByCompany(company);
    }

    public com.ar.nxg.nxgappts.domain.Service findById(Long serviceId) {
        return serviceRepository.findById(serviceId).orElseThrow();
    }

    public List<com.ar.nxg.nxgappts.domain.Service> findAll() {
        return serviceRepository.findAll();
    }
}

