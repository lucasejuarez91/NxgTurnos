package com.ar.nxg.nxgappts.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sec_menu")
public class Menu extends BaseEntity {

    private int ordering;
    private String name;
    private String icon;
    @OneToMany(mappedBy = "parentMenu")
    private List<MenuItem> items;

}
