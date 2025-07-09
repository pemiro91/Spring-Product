package com.pablospring.miapi.service;

import com.pablospring.miapi.domain.event.ProductCreateEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    public void sendEmailProductCreated(String destiny, ProductCreateEvent event) throws MessagingException {
        Context context = new Context();
        context.setVariable("id", event.productId());
        context.setVariable("nombre", event.name());
        context.setVariable("precio", event.price());
        context.setVariable("imagenUrl", event.imagenUrl());

        // Cargar e insertar el CSS como variable Thymeleaf
        context.setVariable("inlineCss", loadCssText());

        String contentHtml = templateEngine.process("email/product-created", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destiny);
        helper.setSubject("📦 Nuevo producto creado");
        helper.setText(contentHtml, true); // true indica que es HTML
        helper.setFrom("pemiro91@gmail.com");

        mailSender.send(message);
    }

    private String loadCssText() {
        try (InputStream inputStream = getClass().getResourceAsStream("/static/styles.css")) {
            if (inputStream == null) return "";
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar el CSS del email", e);
        }
    }
}
