package com.ar.nxg.nxgappts.dto;

import com.ar.nxg.nxgappts.domain.Appointment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AppointmentDTO {
    private String code;
    private String clientName;
    private String companyName;
    private String companyAddress;
    private LocalDateTime start;
    private LocalDateTime end;
    private String service;
    private String professional;
    private BigDecimal price;
    private String apptStatus;

    public AppointmentDTO(Appointment appt) {
        this.code = appt.getCode();
        this.clientName = appt.getClient().getFullname();
        this.companyName = appt.getCompany().getName();
        this.companyAddress = appt.getCompany().getAddress();
        this.service = appt.getService().getName();
        this.professional = appt.getProfessional().getFullname();
        this.start = appt.getScheduledDateStart();
        this.end = appt.getScheduledDateEnd();
        this.price = appt.getService().getPrice();
        this.apptStatus = appt.getApptStatus().getText();
    }

    // Getters y setters

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getApptStatus() {
        return apptStatus;
    }

    public void setApptStatus(String apptStatus) {
        this.apptStatus = apptStatus;
    }
}

