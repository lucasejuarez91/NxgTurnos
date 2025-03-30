package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "appointments", collectionResourceRel = "appointments")
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByCompanyOrderByApptStatusDesc(Company company);

    List<Appointment> findByClient(User userIdLogged);

    List<Appointment> findByProfessional(Professional professional);

    Appointment getAppointmentByCode(String code);
}

