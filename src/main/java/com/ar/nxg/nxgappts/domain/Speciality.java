package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Speciality extends BaseEntity {

    private String name;
    private String description;
    @ManyToMany(mappedBy = "specialities")
    private List<Professional> professionals = new ArrayList<>();

}
