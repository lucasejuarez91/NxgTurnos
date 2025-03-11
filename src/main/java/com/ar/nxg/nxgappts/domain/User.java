package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sec_user")
public class User extends BaseEntity implements UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 493792150501588912L;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    private String nationalId;

    private String phone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    private Boolean neverRevalidate;

    private String confirmationToken;

    private String resetPasswordToken;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_company",  // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "user_id"),  // Clave foránea hacia User
            inverseJoinColumns = @JoinColumn(name = "company_id")  // Clave foránea hacia Company
    )
    private List<Company> companies;  // Empresas asociadas a este usuario

    @OneToOne
    @JoinColumn(name = "avatar_file_id", referencedColumnName = "id")
    private Files avatar; // Relación con un avatar

    public String getFullname() {
        return String.format("%s %s", this.firstName, this.lastName);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

}

