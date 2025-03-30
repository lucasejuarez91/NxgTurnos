package com.ar.nxg.nxgappts.repositories;

import com.ar.nxg.nxgappts.domain.Parameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParametersRepository extends JpaRepository<Parameters, Long> {

    @Query("SELECT p FROM Parameters p WHERE p.name LIKE CONCAT(:valueStartWith, '%')")
    List<Parameters> findStartWith(@Param("valueStartWith") String valueStartWith);

    Parameters findByName(String name);

    List<Parameters> findByReference(String reference);
}
