package com.natuvida.store.config;

import com.natuvida.store.security.RateLimitInterceptor;
import com.natuvida.store.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Autowired
  private RateLimitInterceptor rateLimitInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // Aplica rate limiting a rutas de autenticación para prevenir ataques de fuerza bruta
    registry.addInterceptor(rateLimitInterceptor)
        .addPathPatterns(ApiPaths.LOGIN, ApiPaths.REGISTER, ApiPaths.AUTH + "/**", ApiPaths.OAUTH2 + "/**");
  }
}