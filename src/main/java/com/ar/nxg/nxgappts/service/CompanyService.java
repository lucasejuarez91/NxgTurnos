package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.PaymentItem;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.ar.nxg.nxgappts.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService extends BaseService<Company> {

    @Autowired
    private CompanyRepository companyRepository;

    public Company findById(Long salonId) {
        return companyRepository.findById(salonId).orElseThrow();
    }

    public List<Company> findByNameContains(String name) {
        return companyRepository.findByNameContains(name);
    }

    public List<Company> findAllByStatus(boolean b) {
        return companyRepository.findAllByStatus(b);
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }
}
