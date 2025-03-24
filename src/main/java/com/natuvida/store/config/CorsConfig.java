package com.natuvida.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Value("${cors.allowed-origins}")
  private String[] allowedOrigins;

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    for (String origin : allowedOrigins) {
      config.addAllowedOrigin(origin);
    }

    config.addAllowedMethod("GET");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("PATCH");
    config.addAllowedMethod("OPTIONS");

    config.addAllowedHeader("*");

    // Allow credentials (cookies, authorization headers)
    config.setAllowCredentials(true);

    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}