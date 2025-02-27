package com.ar.nxg.nxgappts.repositories;


import com.ar.nxg.nxgappts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
