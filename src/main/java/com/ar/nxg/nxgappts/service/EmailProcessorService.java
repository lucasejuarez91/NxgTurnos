package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.PendingEmail;
import com.ar.nxg.nxgappts.repositories.PendingEmailRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

@Service
public class EmailProcessorService {

    private final PendingEmailRepository emailRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    public EmailProcessorService(PendingEmailRepository emailRepository, JavaMailSender mailSender, TemplateEngine templateEngine, ObjectMapper objectMapper) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 15000) // Ejecuta cada 1 minuto
    public void processPendingEmails() {
        List<PendingEmail> emails = emailRepository.findByStatus("PENDING");

        for (PendingEmail email : emails) {
            try {
                // Convertir JSON de la BD a Map
                Map<String, Object> model = objectMapper.readValue(email.getTemplateData(), Map.class);

                // Renderizar HTML del email con Thymeleaf
                Context context = new Context();
                context.setVariables(model);
                String body = templateEngine.process(email.getTemplateName(), context);

                // Enviar el email
                sendEmail(email.getRecipient(), email.getSubject(), body);

                email.setStatus("SENT");
            } catch (Exception e) {
                email.setAttempts(email.getAttempts() + 1);
                if (email.getAttempts() > 3) {
                    email.setStatus("FAILED");
                }
            }
            emailRepository.save(email);
        }
    }

    private void sendEmail(String to, String subject, String body) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }
}

