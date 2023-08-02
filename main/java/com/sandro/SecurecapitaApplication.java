package com.sandro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class SecurecapitaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurecapitaApplication.class, args);
    }

    private final List<String> ALLOWED_ORIGINS = Arrays.asList("http://localhost:4200", "http://localhost:3000", "http://securecapita.org");
    private final List<String> ALLOWED_HEADERS = Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type", "Accept",
            "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers");
    private final List<String> EXPOSED_HEADERS = Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "File-Name");

    private final List<String> ALLOWED_METHODS = Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        int strength = 12;
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(ALLOWED_ORIGINS);
        corsConfiguration.setAllowedHeaders(ALLOWED_HEADERS);
        corsConfiguration.setExposedHeaders(EXPOSED_HEADERS);
        corsConfiguration.setAllowedMethods(ALLOWED_METHODS);
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(corsConfigurationSource);
    }

}
