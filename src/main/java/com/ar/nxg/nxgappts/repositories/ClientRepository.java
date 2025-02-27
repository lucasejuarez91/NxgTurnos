package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Client;
import com.ar.nxg.nxgappts.projection.ClientProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "clients", collectionResourceRel = "clients")
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<ClientProjection> findAllBy();
}
