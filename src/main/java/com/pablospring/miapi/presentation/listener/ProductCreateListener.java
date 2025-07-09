package com.pablospring.miapi.presentation.listener;

import com.pablospring.miapi.domain.event.ProductCreateEvent;

import com.pablospring.miapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreateListener {

    private final EmailService emailService;

    @EventListener
    public void handleProductCreated(ProductCreateEvent event) {
        try {
            emailService.sendEmailProductCreated("habanatrans16@gmail.com", event);
            log.info("Correo HTML enviado correctamente para producto {}", event.productId());
        } catch (Exception e) {
            log.error("Error al enviar correo HTML: {}", e.getMessage());
        }
    }
}
