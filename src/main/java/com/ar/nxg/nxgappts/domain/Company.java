package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "sec_company")
public class Company extends BaseEntity {

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<User> users; // Usuarios asociados a esta compania

    @Column(nullable = false)
    private String name;
    private String description;
    private String address;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "company_socialmedia", joinColumns = @JoinColumn(name = "company_id"), inverseJoinColumns = @JoinColumn(name = "social_media_id"))
    private List<SocialMedia> socialMedia;
    private String telephone;
    private String contact;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    private LocalTime minStartTime;
    private LocalTime maxEndtime;

    @OneToOne
    @JoinColumn(name = "banner_file_id", referencedColumnName = "id", nullable = true)
    private Files banner; // Relación con la firma

    @OneToOne
    @JoinColumn(name = "logo_file_id", referencedColumnName = "id", nullable = true)
    private Files logo; // Relación con el logo

}
