package com.natuvida.store.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
  @NotBlank(message = "Debe indicar un correo electrónico")
  @Email(message = "El correo electrónico debe ser un correo válido")
  private String email;

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
      message = "La contraseña debe contener al menos un número, una letra mayúscula, una letra minúscula, un carácter especial y no debe contener espacios"
  )
  private String password;
}