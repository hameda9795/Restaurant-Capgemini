package com.example.Restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The WebConfig class is responsible for configuring web-related settings for the application.
 * It implements the WebMvcConfigurer interface to customize the default Spring MVC configuration.
 *
 * Main Responsibilities:
 * - Define resource handlers for serving static resources.
 *
 * Component Relationships:
 * - WebMvcConfigurer: Interface that provides callback methods to customize the Java-based configuration for Spring MVC.
 *
 * Required Dependencies:
 * - Spring Web
 *
 * Security Notes:
 * - Ensure that the resource locations specified do not expose sensitive files.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures resource handlers to serve static resources.
     * Maps the "/uploads/**" URL pattern to the "src/main/resources/static/uploads/" directory.
     *
     * @param registry the ResourceHandlerRegistry used to register resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the "/uploads/**" URL pattern to the "src/main/resources/static/uploads/" directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:src/main/resources/static/uploads/");
    }
}