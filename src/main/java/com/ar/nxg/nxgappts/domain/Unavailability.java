package com.ar.nxg.nxgappts.domain;

import com.ar.nxg.nxgappts.enums.UnavailabilityType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Unavailability extends BaseEntity {

    @ManyToOne
    private Professional professional;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private UnavailabilityType type; // VACATION, SICK_LEAVE, OTHER

    private String reason;

    public boolean overlaps(LocalDateTime slotStart, LocalDateTime slotEnd) {
        return startDate.isBefore(slotEnd) && endDate.isAfter(slotStart);
    }

}
