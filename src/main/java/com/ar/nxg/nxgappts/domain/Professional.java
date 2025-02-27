package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Professional extends BaseEntity {

    private String firstname;
    private String lastname;
    private String telephone;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "professional_speciality", joinColumns = @JoinColumn(name = "professional_id"), inverseJoinColumns = @JoinColumn(name = "speciality_id"))
    private List<Speciality> specialities;

}
