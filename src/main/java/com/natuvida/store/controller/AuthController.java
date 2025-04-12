package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.request.RefreshTokenRequestDTO;
import com.natuvida.store.dto.request.UserLoginRequestDTO;
import com.natuvida.store.dto.request.UserRequestDTO;
import com.natuvida.store.dto.response.AuthResponseDTO;
import com.natuvida.store.dto.response.UserResponseDTO;
import com.natuvida.store.entity.User;
import com.natuvida.store.enums.Role;
import com.natuvida.store.mapper.UserMapper;
import com.natuvida.store.repository.UserRepository;
import com.natuvida.store.security.CustomUserDetailsService;
import com.natuvida.store.security.JwtTokenProvider;
import com.natuvida.store.service.UserService;
import com.natuvida.store.util.ApiPaths;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.AUTH)
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> authenticateUser(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userLoginRequestDTO.getEmail(),
            userLoginRequestDTO.getPassword()
        )
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    User userDetails = (User) authentication.getPrincipal();

    // Generate JWT tokens
    String jwt = jwtTokenProvider.generateToken(authentication);
    String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

    String redirectUrl = determineRedirectUrl(userDetails.getRole());
    UserResponseDTO userResponseDTO = userMapper.toDto(userDetails);
    AuthResponseDTO authResponse = new AuthResponseDTO(jwt, refreshToken, userResponseDTO, redirectUrl);
    return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
  }

  private String determineRedirectUrl(Role role) {
    if (role == Role.ADMIN) {
      return "/admin/dashboard";
    } else {
      return "/mi-cuenta";
    }
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
    boolean userExists = userRepository.existsByEmail(userRequestDTO.getEmail());
    // Ejecutamos la codificación de contraseña para evitar timing attacks
    passwordEncoder.encode(userRequestDTO.getPassword());
    if (userExists) {
      return ResponseEntity.ok(
          ApiResponse.success(null, "Si el email no está registrado, se enviará un correo de verificación")
      );
    }
    UserResponseDTO registeredUser = userService.registerUser(userRequestDTO);
    return ResponseEntity.ok(
        ApiResponse.success(registeredUser, "Si el email no está registrado, se enviará un correo de verificación")
    );
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logoutUser(HttpServletRequest request, HttpServletResponse response) {
    // Obtener el objeto Authentication actual
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      // Invalidar la sesión HTTP si existe
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }
      // Limpiar el contexto de seguridad
      SecurityContextHolder.clearContext();
    }
    return ResponseEntity.ok(ApiResponse.success(null, "Sesión cerrada exitosamente"));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
    // Get the refresh token from the request
    String refreshToken = refreshTokenRequest.getRefreshToken();

    // Add basic validation
    if (refreshToken == null || refreshToken.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Refresh token is required"));
    }

    try {
      // Validate the refresh token
      if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Invalid or expired refresh token"));
      }

      // Extract username from the refresh token
      String username = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);
      if (username == null || username.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Invalid token - cannot extract username"));
      }

      // Load the user details
      UserDetails userDetails;
      try {
        userDetails = customUserDetailsService.loadUserByUsername(username);
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not found or no longer active"));
      }

      // Create new authentication object
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());

      // Generate new tokens
      String newAccessToken = jwtTokenProvider.generateToken(authentication);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

      // Create user DTO
      User user = (User) userDetails;
      UserResponseDTO userResponseDTO = userMapper.toDto(user);

      // Determine redirect URL
      String redirectUrl = determineRedirectUrl(user.getRole());

      // Build response object
      AuthResponseDTO authResponse = new AuthResponseDTO(
          newAccessToken,
          newRefreshToken,
          userResponseDTO,
          redirectUrl
      );

      return ResponseEntity.ok(ApiResponse.success(authResponse, "Token refreshed successfully"));

    } catch (Exception e) {
      // Log the exception for debugging
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("An error occurred during token refresh: " + e.getMessage()));
    }
  }
}