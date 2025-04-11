package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.request.UserProfileRequestDTO;
import com.natuvida.store.dto.response.UserDTO;
import com.natuvida.store.service.UserService;
import com.natuvida.store.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiPaths.USERS)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

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

  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(@RequestBody UserProfileRequestDTO profileData) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
      UUID userId = userService.getUserIdByEmail(username);
      return ResponseEntity.ok(ApiResponse.success(userService.updateUser(userId, profileData), "Perfil actualizado correctamente." ));
  }
}