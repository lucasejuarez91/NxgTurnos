package com.ar.nxg.nxgappts;

import com.ar.nxg.nxgappts.repositories.ParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.ar.nxg.nxgappts.domain.Parameters;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class MailConfig {

    @Autowired
    private ParametersRepository parametersRepository;

    @Bean
    public JavaMailSender javaMailSender() {

        Map<String, String> params = parametersRepository.findByReference("MAIL").stream()
                .collect(Collectors.toMap(Parameters::getName, Parameters::getValue));
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        System.out.println("SMTP Server: " + params.get("SMTP.SERVER"));
        System.out.println("SMTP Port: " + params.get("SMTP.PORT"));
        if (!params.isEmpty()) {
            mailSender.setHost(params.get("SMTP.SERVER"));
            mailSender.setPort(Integer.parseInt(params.get("SMTP.PORT")));
            mailSender.setUsername(params.get("SMTP.USER"));
            mailSender.setPassword(params.get("SMTP.PASSWORD"));
            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "false"); // STARTTLS no es necesario si usas SSL puro
            props.put("mail.debug", "true");
            props.put("mail.smtp.ssl.enable", "true"); // Habilitar SSL
            props.put("mail.smtp.socketFactory.port", params.get("SMTP.PORT")); // Configurar el puerto para la conexión segura
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // Clase para manejar SSL
            props.put("mail.smtp.socketFactory.fallback", "false");
        }
        return mailSender;
    }
}

