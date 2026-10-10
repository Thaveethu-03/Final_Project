package com.example.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/** Adds the "Authorize" button in Swagger UI so the JWT token can be pasted once. */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Real Estate Management API", version = "1.0",
                description = "Owners, "
                		          + "properties, "
                		          + "tenants, "
                		          + "leases, "
                		          + "rent payments and "
                		          + "maintenance requests"),
        
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")

public class OpenAiConfig 
{
	
}
