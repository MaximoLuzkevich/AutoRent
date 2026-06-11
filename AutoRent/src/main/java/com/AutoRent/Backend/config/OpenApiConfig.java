package com.AutoRent.Backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autoRentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoRent API")
                        .description("API REST para alquiler de autos")
                        .version("1.0"));
    }
}
