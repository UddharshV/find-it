package com.uddharsh.findit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                              // which URLs
                .allowedOrigins("http://localhost:4200")            // who may call them
                .allowedMethods("GET", "POST", "PUT", "DELETE")     // which HTTP methods
                .allowedHeaders("Content-Type", "X-User-Id");       // which request headers
    }
}