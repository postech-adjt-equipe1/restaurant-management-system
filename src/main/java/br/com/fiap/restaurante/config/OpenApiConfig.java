package br.com.fiap.restaurante.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Restaurante — Gestão de Usuários")
                .version("1.0")
                .description("API para cadastro e gerenciamento de usuários do sistema de restaurantes. " +
                             "POSTECH FIAP — Arquitetura e Desenvolvimento Java — Fase 1"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token JWT obtido via POST /api/v1/usuarios/login")));
    }
}
