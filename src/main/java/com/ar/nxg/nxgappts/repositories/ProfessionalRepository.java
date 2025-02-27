package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "professionals", collectionResourceRel = "professionals")
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {}

