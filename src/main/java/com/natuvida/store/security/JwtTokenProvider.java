package com.natuvida.store.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// TODO: REVIEW THIS IMPLEMENTATION
@Component
public class JwtTokenProvider {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration}")
  private long jwtExpirationMs;

  @Value("${app.jwt.refresh.expiration}")
  private long refreshTokenExpirationMs;

  @Value("${app.jwt.refresh.secret:${app.jwt.secret}}")
  private String refreshTokenSecret;

  @Value("${app.jwt.refresh.secret:}")
  private String refreshTokenSecretOverride;

  // Constructor sin parámetros RSA
  public JwtTokenProvider() {
    // Constructor vacío
  }

  // Generate an access token
  public String generateToken(Authentication authentication) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    String username = authentication.getName();
    // Obtener el único rol del usuario (si existe)
    String role = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .findFirst()
        .orElse(null);

    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

    JwtBuilder builder = Jwts.builder()
        .setSubject(username)
        .claim("tokenType", "access")
        .setIssuedAt(now)
        .setExpiration(expiryDate);

    if (role != null && !role.startsWith("ROLE_")) {
      role = "ROLE_" + role;
    }

    if (role != null) {
      builder.claim("role", role);
    }

    return builder.signWith(key).compact();
  }

  // Generate a refresh token
  public String generateRefreshToken(Authentication authentication) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);
    String username = authentication.getName();

    SecretKey key = Keys.hmacShaKeyFor(getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .setSubject(username)
        .claim("tokenType", "refresh")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key)
        .compact();
  }

  // Get username from access token
  public String getUsernameFromToken(String token) {
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  public String getUsernameFromRefreshToken(String token) {
    SecretKey key = Keys.hmacShaKeyFor(getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8));

    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  // Validate access token
  public boolean validateToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      // Optionally check that this is indeed an access token
      String tokenType = (String) claims.get("tokenType");
      return "access".equals(tokenType);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // Validate refresh token
  public boolean validateRefreshToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8));

      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      // Check that this is indeed a refresh token
      String tokenType = (String) claims.get("tokenType");
      return "refresh".equals(tokenType);
    } catch (JwtException | IllegalArgumentException e) {
      System.err.println("Refresh token validation failed: " + e.getMessage());
      return false;
    }
  }

  private String getRefreshTokenSecret() {
    return (refreshTokenSecretOverride != null && !refreshTokenSecretOverride.isEmpty())
        ? refreshTokenSecretOverride
        : jwtSecret;
  }
}