package com.natuvida.store.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String ipAddress = getClientIP(request);

    // Crear bucket para esta IP si no existe
    Bucket bucket = buckets.computeIfAbsent(ipAddress, ip -> createNewBucket());

    // Intentar consumir un token
    if (bucket.tryConsume(1)) {
      return true;
    } else {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"error\": \"Too many requests\"}");
      return false;
    }
  }

  private Bucket createNewBucket() {
    // Permite 10 peticiones cada minuto
    return Bucket.builder()
        .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
        .build();
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}