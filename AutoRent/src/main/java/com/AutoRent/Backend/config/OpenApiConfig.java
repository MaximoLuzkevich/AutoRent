package com.AutoRent.Backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autoRentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoRent API")
                        .description("""
                                API REST para una plataforma de alquiler de autos.

                                Flujo recomendado:
                                1. Registrar usuario o iniciar sesion.
                                2. Copiar el token JWT devuelto por /api/usuarios/login.
                                3. Usar el boton Authorize con el formato Bearer JWT.
                                4. Probar endpoints protegidos segun el rol: CLIENTE, PROPIETARIO o ADMINISTRADOR.

                                Los endpoints /me usan el usuario autenticado y son los recomendados para operaciones propias.
                                """)
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
