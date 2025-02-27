package com.ar.nxg.nxgappts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Getter
    @LastModifiedDate
    @Column(name = "updated_date")
    private Date updatedDate;

    @Getter
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", updatable = false)
    private User creator;

    @Getter
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changer")
    private User changer;

    @Column(name = "status")
    private boolean status = true;

    public boolean getStatus() {
        return status;
    }

}
