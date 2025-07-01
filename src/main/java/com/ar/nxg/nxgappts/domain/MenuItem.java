package com.ar.nxg.nxgappts.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sec_menu_item")
public class MenuItem extends BaseEntity {

    private String title;
    private String url;
    private String icon;
    private int ordering;
    private String breadcrumb;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu parentMenu;

    @ManyToMany
    @JoinTable(name = "role_menu_item", joinColumns = @JoinColumn(name = "menu_item_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public List<String> getBreadcrumbList() {
        return this.breadcrumb.isEmpty() ? List.of() : List.of(this.breadcrumb.split(";"));
    }
}
