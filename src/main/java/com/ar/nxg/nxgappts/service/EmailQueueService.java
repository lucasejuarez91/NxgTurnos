package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.PendingEmail;
import com.ar.nxg.nxgappts.repositories.PendingEmailRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailQueueService {

    private final PendingEmailRepository emailRepository;
    private final ObjectMapper objectMapper;

    public EmailQueueService(PendingEmailRepository emailRepository, ObjectMapper objectMapper) {
        this.emailRepository = emailRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void queueEmail(String recipient, String subject, String templateName, Map<String, Object> templateData) {
        try {
            PendingEmail email = new PendingEmail();
            email.setRecipient(recipient);
            email.setSubject(subject);
            email.setTemplateName(templateName);
            email.setTemplateData(objectMapper.writeValueAsString(templateData));
            email.setStatus("PENDING");
            emailRepository.save(email);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar el correo a la cola", e);
        }
    }

    public List<PendingEmail> getPendingEmails() {
        return emailRepository.findByStatus("PENDING");
    }
}

