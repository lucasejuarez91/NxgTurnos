package com.ar.nxg.nxgappts.domain;

import com.ar.nxg.nxgappts.enums.SocialMediaTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class SocialMedia extends BaseEntity {

    private String name;
    private SocialMediaTypeEnum type;

    @ManyToMany(mappedBy = "socialMedia")
    private List<Company> companies = new ArrayList<>();

}
