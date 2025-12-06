package com.lab4.calculator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Calculadora de Notas Mínimas",
                version = "v1",
                description = "API para simulação de notas seguindo Clean Architecture"
        ),
        servers = {@Server(url = "/", description = "Ambiente local")}
)
public class OpenApiConfig {
    // Configuração simples apenas para publicação do Swagger UI em /swagger-ui.html
}
