package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Files;
import com.ar.nxg.nxgappts.domain.Item;
import com.ar.nxg.nxgappts.repositories.FilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FilesService extends BaseService<Files> {

    @Autowired
    private FilesRepository filesRepository;
    public Files findById(Long fileId) {
        return filesRepository.findById(fileId).orElseThrow();
    }
}
