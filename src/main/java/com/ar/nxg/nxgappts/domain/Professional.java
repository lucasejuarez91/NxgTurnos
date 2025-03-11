package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Professional extends BaseEntity {

    private String firstname;
    private String lastname;
    private String telephone;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "professional_service", joinColumns = @JoinColumn(name = "professional_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<Service> services;

    @ManyToOne
    private Company company;

    public String getFullname(){
        return String.format("%s %s", this.firstname, this.lastname);
    }

}
