package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.CategoryService;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.repositories.CategoryServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServicesService extends BaseService<CategoryService> {

    @Autowired
    private CategoryServiceRepository categoryServiceRepository;

    public List<CategoryService> findByCompany(Company company) {
        return categoryServiceRepository.findByCompany(company);
    }

    public CategoryService findByIdAndCompany(long categoryServiceId, Company company) {
        return categoryServiceRepository.findByIdAndCompany(categoryServiceId, company);
    }

    public List<CategoryService> findAll() {
        return categoryServiceRepository.findAll();
    }
}

