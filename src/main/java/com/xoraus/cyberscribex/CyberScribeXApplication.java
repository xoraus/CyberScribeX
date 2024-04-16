package com.xoraus.cyberscribex;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "CyberScribeX",
                description = "CyberScribeX integrates Posts and Comments management with robust Authentication, Authorization, and Category organization through RESTful APIs, fortified by JWT token-based security.",
                version = "v1.0",
                contact = @Contact(
                        name = "Sajjad Salaria",
                        email = "xoraus@protonmail.com",
                        url = "xoraus.github.io"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "xoraus.github.io/licence"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "CyberScribeX API Codebase",
                url = "https://github.com/xoraus/CyberScribeX"
        )
)
public class CyberScribeXApplication {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(CyberScribeXApplication.class, args);
    }

}
