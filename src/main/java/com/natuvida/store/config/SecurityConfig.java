package com.natuvida.store.config;

import com.natuvida.store.security.CustomOAuth2UserService;
import com.natuvida.store.security.CustomUserDetailsService;
import com.natuvida.store.util.ApiPaths;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

// authentication, authorization, CORS, CSRF, and OAuth2.
@Configuration // Marks this as a configuration class that provides bean definitions
@EnableWebSecurity // Enables Spring Security web security support
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomUserDetailsService customUserDetailsService;

  @Value("${app.jwt.secret}")
  private String jwtSecret;


  public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomUserDetailsService customUserDetailsService) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.customUserDetailsService = customUserDetailsService;
  }

  /**
   * Defines the security filter chain that determines how HTTP requests are secured.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Configure Cross-Origin Resource Sharing (CORS)
        // This allows your Next.js frontend to communicate with this API
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        // Configure Cross-Site Request Forgery (CSRF) protection
        // This generates a token that must be included in state-changing requests (POST, PUT, DELETE)
        // withHttpOnlyFalse() allows JavaScript to read the cookie value
        .csrf(csrf -> csrf
            .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringRequestMatchers(ApiPaths.LOGIN, ApiPaths.REGISTER, ApiPaths.REFRESH_TOKEN)
        )

        // Configure session management
        // STATELESS means no session will be created or used by Spring Security
        // This is appropriate for REST APIs that use token-based authentication
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint((request, response, authException) -> {
              if (request.getRequestURI().startsWith("/api/") ||
                  request.getHeader("Accept") != null &&
                      request.getHeader("Accept").contains("application/json")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\"}");
              } else {
                response.sendRedirect("/login");
              }
            })
        )

        // Configure authorization rules for different URL patterns
        .authorizeHttpRequests(authorize -> authorize
            // Public endpoints that don't require authentication
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers(ApiPaths.LOGIN).permitAll()
            .requestMatchers(ApiPaths.REGISTER).permitAll()
            .requestMatchers(ApiPaths.REFRESH_TOKEN).permitAll()
            // Read-only endpoints for products and categories
            .requestMatchers(HttpMethod.GET, ApiPaths.CATEGORIES + "/**").permitAll()
            .requestMatchers(HttpMethod.GET, ApiPaths.PRODUCTS + "/**").permitAll()
            // Admin-only operations for products and categories
            .requestMatchers(HttpMethod.POST, ApiPaths.PRODUCTS + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, ApiPaths.PRODUCTS + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, ApiPaths.PRODUCTS + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, ApiPaths.CATEGORIES + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, ApiPaths.CATEGORIES + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, ApiPaths.CATEGORIES + "/**").hasRole("ADMIN")
            // Admin-only access to customer management
            .requestMatchers(HttpMethod.GET, ApiPaths.CUSTOMERS + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, ApiPaths.CUSTOMERS + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, ApiPaths.CUSTOMERS + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, ApiPaths.CUSTOMERS + "/**").hasRole("ADMIN")
            // Admin can access all order operations
            .requestMatchers(HttpMethod.GET, ApiPaths.ORDERS).hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, ApiPaths.ORDERS + "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, ApiPaths.ORDERS + "/**").hasRole("ADMIN")
            // Users can create orders and view specific orders (their own)
            .requestMatchers(HttpMethod.POST, ApiPaths.ORDERS).authenticated()
            .requestMatchers(HttpMethod.GET, ApiPaths.ORDERS + "/**").authenticated()
            // Users can access and update their own profile
            .requestMatchers(HttpMethod.GET, ApiPaths.USERS + "/me").authenticated()
            .requestMatchers(HttpMethod.PUT, ApiPaths.USERS + "/me").authenticated()

            // All other endpoints require admin permission
            .anyRequest().hasRole("ADMIN")
        )

        // Configure OAuth2 resource server support
        // This allows validation of JWT tokens from OAuth providers
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
            )
        )

        // Configure OAuth2 login support (e.g., "Login with Google")
        .oauth2Login(oauth2 -> oauth2
            // Redirect URL after successful authentication
            .defaultSuccessUrl("/api/auth/oauth2/success", true)
            // Redirect URL after failed authentication
            .failureUrl("/api/auth/oauth2/failure")
            // Custom user service to process OAuth2 users
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
        )
        .authenticationProvider(authenticationProvider());

    return http.build();
  }

  /**
   * Configures CORS settings to allow the frontend to communicate with the backend.
   * CORS is crucial for applications where frontend and backend are on different domains.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Defines which origins can access the API (your Next.js app URL)
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));

    // Defines which HTTP methods can be used (GET, POST, etc.)
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // Defines which headers can be included in requests
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-XSRF-TOKEN"));

    // Defines which headers the browser can expose to JavaScript
    configuration.setExposedHeaders(List.of("X-XSRF-TOKEN"));

    // Allows cookies and authentication headers to be included in requests
    configuration.setAllowCredentials(true);

    // Apply these settings to all paths
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthoritiesClaimName("role");
    grantedAuthoritiesConverter.setAuthorityPrefix(""); // Quitar el prefijo, ya que tu token ya incluye "ROLE_"

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

//  @Bean
//  public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
//    return JwtDecoders.fromIssuerLocation(properties.getJwt().getIssuerUri());
//  }

  @Bean
  public JwtDecoder jwtDecoder() {
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    return NimbusJwtDecoder.withSecretKey(key)
        .macAlgorithm(MacAlgorithm.HS512)  // Cambiado de HS256 a HS512
        .build();
  }

}