package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "unavailabilities", collectionResourceRel = "unavailabilities")
public interface UnavailableRepository extends JpaRepository<Unavailability, Long> {

    @Query("SELECT u FROM Unavailability u WHERE u.professional.id = :professionalId " +
            "AND u.startDate <= :endDateTime AND u.endDate >= :startDateTime")
    List<Unavailability> findByProfessionalIdAndDateTimeRange(
            @Param("professionalId") Long professionalId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}

