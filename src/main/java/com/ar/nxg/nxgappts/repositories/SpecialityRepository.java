package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.domain.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "speciality", collectionResourceRel = "speciality")
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {}

