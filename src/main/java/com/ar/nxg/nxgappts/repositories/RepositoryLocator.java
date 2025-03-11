package com.ar.nxg.nxgappts.repositories;

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.beans.Introspector;

@Component
public class RepositoryLocator {

    private final ApplicationContext applicationContext;

    public RepositoryLocator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T, ID> JpaRepository<T, ID> getRepository(Class<T> entityType) {
        String beanName = Introspector.decapitalize(entityType.getSimpleName()) + "Repository";
        return (JpaRepository<T, ID>) applicationContext.getBean(beanName);
    }
}
