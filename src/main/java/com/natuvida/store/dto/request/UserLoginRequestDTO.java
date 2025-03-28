package com.natuvida.store.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDTO {
  @NotBlank(message = "El email es obligatorio")
  @Email(message = "Debe proporcionar un email válido")
  private String email;

  @NotBlank(message = "La contraseña es obligatoria")
  private String password;
}