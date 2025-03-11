package com.ar.nxg.nxgappts.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class ImageController {

    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<FileSystemResource> getImage(@PathVariable String filename) {
        File file = new File(uploadDir, filename);
        if (file.exists()) {
            return ResponseEntity.ok(new FileSystemResource(file));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
