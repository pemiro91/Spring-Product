package com.pablospring.miapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MiApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiApiApplication.class, args);
    }

}
