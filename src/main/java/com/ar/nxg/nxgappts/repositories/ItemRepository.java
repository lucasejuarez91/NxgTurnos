package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Item;
import com.ar.nxg.nxgappts.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "items", collectionResourceRel = "items")
public interface ItemRepository extends JpaRepository<Item, Long> {

    Item findByName(String reservation);

    List<Item> findByCompany(Company company);

    Item findByNameAndCompany(String reservation, Company company);
}

