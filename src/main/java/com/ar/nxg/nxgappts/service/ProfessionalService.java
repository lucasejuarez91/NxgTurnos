package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.repositories.ProfessionalRepository;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessionalService extends BaseService<Professional> {

    private final ProfessionalRepository professionalRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    public List<Professional> findByCompanyAndStatus(HttpSession httpSession, boolean enabled) {
        return professionalRepository.findByCompanyAndStatus((Company) httpSession.getAttribute("actualCompany"), enabled);
    }

    public List<Professional> findAllByCompany(Company company) {
        return professionalRepository.findAllByCompany(company);
    }

    public Professional findById(Long professionalId) {
        return professionalRepository.findById(professionalId).orElseThrow();
    }

    public List<Professional> findByCompanyAndServices(Company company, com.ar.nxg.nxgappts.domain.Service service) {
        return professionalRepository.findByCompanyAndServices(company, Optional.ofNullable(service));
    }
}

