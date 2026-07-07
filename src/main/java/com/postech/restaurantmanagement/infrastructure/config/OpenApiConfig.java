package com.postech.restaurantmanagement.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI restaurantManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Management System API")
                        .description("""
                                API para gestão de restaurantes: tipos de usuário, usuários, \
                                restaurantes e itens de cardápio. Desenvolvida como Tech Challenge \
                                da Fase 2 do curso de Arquitetura e Desenvolvimento Java (FIAP POS TECH).""")
                        .version("v1")
                        .contact(new Contact().name("Equipe Restaurant Management System"))
                        .license(new License().name("Uso educacional - FIAP POS TECH")));
    }
}
