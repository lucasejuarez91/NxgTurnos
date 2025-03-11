package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "professionals", collectionResourceRel = "professionals")
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    List<Professional> findByCompanyAndStatus(Company actualCompanyEntity, boolean enabled);

    List<Professional> findAllByCompany(Company actualCompany);

    List<Professional> findByCompanyAndServices(Company company, Optional<Service> byId);
}

