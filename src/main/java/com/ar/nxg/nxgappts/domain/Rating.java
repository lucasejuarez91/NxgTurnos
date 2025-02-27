package com.ar.nxg.nxgappts.domain;

import com.ar.nxg.nxgappts.enums.SocialMediaTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Rating extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Un Rating pertenece a un único usuario
    private User user;

    private int star;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false) // Un Rating pertenece a una sola empresa
    private Company company;

}
