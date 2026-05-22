package com.xoraus.cyberscribex;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
        info = @Info(
                title = "CyberScribeX",
                description = "Production-grade Blogging Platform REST API with JWT authentication, role-based access control, and comprehensive CRUD operations.",
                version = "v1.0",
                contact = @Contact(
                        name = "Sajjad Salaria",
                        email = "xoraus@protonmail.com",
                        url = "https://xoraus.github.io"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "CyberScribeX API Codebase",
                url = "https://github.com/xoraus/CyberScribeX"
        )
)
public class CyberScribeXApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyberScribeXApplication.class, args);
    }
}
