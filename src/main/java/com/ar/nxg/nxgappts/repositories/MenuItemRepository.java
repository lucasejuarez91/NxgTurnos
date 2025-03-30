package com.ar.nxg.nxgappts.repositories;


import com.ar.nxg.nxgappts.domain.MenuItem;
import com.ar.nxg.nxgappts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    MenuItem findByUrl(String viewName);
}
