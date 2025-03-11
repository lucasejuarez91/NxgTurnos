package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.dto.ResponseMessage;
import com.ar.nxg.nxgappts.service.FilesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/file-upload")
public class FileUploadController extends GlobalControllerAdvice {

    private static final Logger logger = LogManager.getLogger(FileUploadController.class);

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private FilesService filesService;

    @PostMapping("/")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return buildMessageResponse(true, "El archivo está vacío.");
        }

        try {
            // Obtener el nombre del archivo
            String fileName = file.getOriginalFilename();

            // Ruta completa para guardar el archivo
            Path filePath = Paths.get(uploadDir + File.separator + fileName);

            // Crear directorios si no existen
            Files.createDirectories(filePath.getParent());

            // Guardar el archivo
            Path path = Files.write(filePath, file.getBytes());
            if(path.getFileName() == null) {
                return buildMessageResponse(true, "No se pudo subir el archivo.");
            }
            com.ar.nxg.nxgappts.domain.Files ff = new com.ar.nxg.nxgappts.domain.Files();
            ff.setName(path.toFile().getName());
            ff.setPath(path.toFile().getAbsolutePath());
            ff = (com.ar.nxg.nxgappts.domain.Files) filesService.saveOrUpdate(ff);
            return buildMessageResponse(false, ff.getId().toString());
        } catch (IOException e) {
            return buildMessageResponse(true, "No se pudo crear la entidad");
        }
    }
}
