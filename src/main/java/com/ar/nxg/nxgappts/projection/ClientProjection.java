package com.ar.nxg.nxgappts.projection;

import com.ar.nxg.nxgappts.domain.Client;
import com.ar.nxg.nxgappts.domain.User;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "simpleListClients", types = Client.class)
public interface ClientProjection {
    Long getId();
    String getFirstname();
    String getLastname();
    String getEmail();
    String getTelephone();
}
