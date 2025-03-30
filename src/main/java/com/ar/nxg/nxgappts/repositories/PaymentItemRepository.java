package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Payment;
import com.ar.nxg.nxgappts.domain.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "paymentItems", collectionResourceRel = "paymentItems")
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {

}

