package br.edu.ifsp.competicoes_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API - Sistema Integrado de Controle de Competições Esportivas",
                version = "1.0.0",
                description = "API REST do MVP desenvolvido para o Instituto Federal de São Paulo (IFSP). " +
                        "Permite o gerenciamento de eventos esportivos, partidas, chaveamento automático de torneios, " +
                        "autenticação de usuários e a área de rede social integrada (comentários e mídias).",
                contact = @Contact(
                        name = "Equipe de Desenvolvimento - IFSP",
                        email = "contato@ifsp.edu.br"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}