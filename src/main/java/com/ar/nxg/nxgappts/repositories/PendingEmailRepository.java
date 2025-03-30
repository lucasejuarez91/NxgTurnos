package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "pendingEmail", collectionResourceRel = "pendingEmail")
public interface PendingEmailRepository extends JpaRepository<PendingEmail, Long> {
    List<PendingEmail> findByStatus(String status);
}

