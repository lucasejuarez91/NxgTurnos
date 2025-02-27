package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Client extends BaseEntity {

    private String firstname;
    private String lastname;
    private String email;
    private String telephone;

    public String getFullname(){
        return String.format("%s %s", this.firstname, this.lastname);
    }
}
