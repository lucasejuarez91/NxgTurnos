package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Payment;
import com.ar.nxg.nxgappts.domain.Service;
import com.ar.nxg.nxgappts.enums.PaymentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "payments", collectionResourceRel = "payments")
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByPaymentCode(String paymentCode);

    List<Payment> findByAppointment(Appointment appt);

    Payment findByAppointmentAndPaymentStatusEnum(Appointment appt, PaymentStatusEnum paymentStatusEnum);

}

