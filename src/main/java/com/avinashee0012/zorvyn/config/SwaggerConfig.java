package com.avinashee0012.zorvyn.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Value("${swagger.server-url}")
	private String swaggerServerUrl;

	@Bean
	public OpenAPI zorvynOpenApi() {
		return new OpenAPI()
				.addServersItem(new Server().url(swaggerServerUrl))
				.info(new Info()
						.title("Zorvyn API")
						.version("v1.0.0")
						.description("Rest APIs for user, financial record, and dashboard modules"))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
				.components(new Components()
						.addSecuritySchemes("bearerAuth", new SecurityScheme()
								.name("bearerAuth")
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")));
	}
}
