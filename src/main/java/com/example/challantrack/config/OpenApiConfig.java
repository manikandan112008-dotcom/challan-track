package com.example.challantrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI challanTrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Challan Track API")
                        .description("Traffic e-challan issue and repeat offender tracker")
                        .version("1.0.0"));
    }
}
