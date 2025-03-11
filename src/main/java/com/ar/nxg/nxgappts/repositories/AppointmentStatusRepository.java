package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "appointmentStatus", collectionResourceRel = "appointmentStatus")
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatus, Long> {
    AppointmentStatus findByName(String creado);
}
