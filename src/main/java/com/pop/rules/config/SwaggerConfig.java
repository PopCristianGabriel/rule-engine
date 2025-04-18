package com.pop.rules.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI caasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Events API")
                        .description("API for managing users and authentication")
                        .version("1.0"));
    }

    @Bean
    public GroupedOpenApi caasApi() {
        return GroupedOpenApi.builder()
                .group("caas") // You can name it anything
                .packagesToScan("com.pop.rules.controllers") // Only scan this package
                .build();
    }
}