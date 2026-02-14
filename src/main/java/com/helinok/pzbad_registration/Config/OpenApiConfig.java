package com.helinok.pzbad_registration.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PZBAD Registration API")
                        .version("1.0")
                        .description("API dla systemu rejestracji turniejów badmintona"));
    }
}