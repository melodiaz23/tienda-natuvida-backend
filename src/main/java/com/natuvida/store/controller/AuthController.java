package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.request.UserLoginRequestDTO;
import com.natuvida.store.dto.request.UserRequestDTO;
import com.natuvida.store.dto.response.UserDTO;
import com.natuvida.store.entity.User;
import com.natuvida.store.mapper.UserMapper;
import com.natuvida.store.repository.UserRepository;
import com.natuvida.store.service.UserService;
import com.natuvida.store.util.ApiPaths;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<UserDTO>> authenticateUser(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userLoginRequestDTO.getEmail(),
            userLoginRequestDTO.getPassword()
        )
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    User userDetails = (User) authentication.getPrincipal();
    UserDTO userDTO = userMapper.toDto(userDetails);
    return ResponseEntity.ok(ApiResponse.success(userDTO, "Inicio de sesión exitoso"));
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
    boolean userExists = userRepository.existsByEmail(userRequestDTO.getEmail());
    // Ejecutamos la codificación de contraseña para evitar timing attacks
    passwordEncoder.encode(userRequestDTO.getPassword());
    if (userExists) {
      return ResponseEntity.ok(
          ApiResponse.success(null, "Si el email no está registrado, se enviará un correo de verificación")
      );
    }
    UserDTO registeredUser = userService.registerUser(userRequestDTO);
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
}