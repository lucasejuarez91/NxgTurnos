package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "services", collectionResourceRel = "services")
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByCompany(Company actualCompanyEntity);
}

