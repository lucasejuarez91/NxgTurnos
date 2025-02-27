package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Availability;
import com.ar.nxg.nxgappts.domain.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "availability", collectionResourceRel = "availability")
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByProfessional(Professional prof);

    List<Availability> findByProfessionalId(Long professionalId);
}

