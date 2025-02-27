package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sec_role")
public class Role extends BaseEntity {

    private String name; // Nombre del rol (ADMIN, USER, etc.)

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

}
