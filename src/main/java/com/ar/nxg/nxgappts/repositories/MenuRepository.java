package com.ar.nxg.nxgappts.repositories;


import com.ar.nxg.nxgappts.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

}
