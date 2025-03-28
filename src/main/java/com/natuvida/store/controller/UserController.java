package com.natuvida.store.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @GetMapping("/me")
  public Map<String, Object> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("username", authentication.getName());
    userInfo.put("isAuthenticated", authentication.isAuthenticated());

    // Extract roles
    userInfo.put("roles", authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList()));

    // Check if user is admin
    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    userInfo.put("isAdmin", isAdmin);

    return userInfo;
  }
}