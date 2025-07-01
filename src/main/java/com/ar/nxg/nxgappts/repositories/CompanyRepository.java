package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Client;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.projection.ClientProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "companies", collectionResourceRel = "companies")
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByNameContains(String name);

    List<Company> findAllByStatus(boolean b);
}
