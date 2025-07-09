package com.pablospring.miapi.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageSource messageSource;

    @GetMapping("/mensaje")
    public String getMensaje(Locale locale) {
        return messageSource.getMessage("welcome.message", null, locale);
    }
}
