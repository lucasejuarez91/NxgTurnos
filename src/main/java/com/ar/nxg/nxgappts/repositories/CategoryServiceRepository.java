package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.CategoryService;
import com.ar.nxg.nxgappts.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "categoryServices", collectionResourceRel = "categoryServices")
public interface CategoryServiceRepository extends JpaRepository<CategoryService, Long> {
    CategoryService findByIdAndCompany(long categoryServiceId, Company actualCompany);

    List<CategoryService> findByCompany(Company company);
}

