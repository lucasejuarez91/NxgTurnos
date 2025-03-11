package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Files;
import com.ar.nxg.nxgappts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "files", collectionResourceRel = "files")
public interface FilesRepository extends JpaRepository<Files, Long> {

}

