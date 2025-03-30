package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Professional;
import com.ar.nxg.nxgappts.domain.Role;
import com.ar.nxg.nxgappts.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "roles", collectionResourceRel = "roles")
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String apptInitiator);
}

