package com.ar.nxg.nxgappts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ar.nxg")
public class NxgApptsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NxgApptsApplication.class, args);
    }

}
