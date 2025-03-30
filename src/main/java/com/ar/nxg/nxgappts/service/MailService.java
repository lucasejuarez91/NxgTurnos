package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Parameters;
import com.ar.nxg.nxgappts.repositories.ParametersRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ParametersRepository parametersRepository;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendSimpleMessage(String to, String subject, String text) {

        Parameters p = parametersRepository.findByName("EMAIL.FROM");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(p.getValue()); // Dirección de correo que envía
        message.setTo(to);  // Dirección de destino
        message.setSubject(subject);  // Asunto del correo
        message.setText(text);  // Contenido del correo

        emailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, Map<String, Object> params, String htmlTemplateName, String companyLogoPath) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Cargar plantilla Thymeleaf
        Context context = new Context();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        String htmlContent = templateEngine.process(htmlTemplateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Agregar logo embebido
        FileSystemResource logo = new FileSystemResource(new File(companyLogoPath)); // Ruta completa del logo
        helper.addInline("logo", logo);

        emailSender.send(message);
    }

}
