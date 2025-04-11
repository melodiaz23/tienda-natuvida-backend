package com.natuvida.store.dto.request;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class UserProfileRequestDTO {
  @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
  private String name;

  @Size(min = 3, message = "El apellido debe contener al menos 3 caracteres")
  private String lastName;

  @Email(message = "El correo electrónico debe ser un correo válido")
  private String email;

  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
      message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número y un carácter especial"
  )
  private String password;

  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
      message = "La contraseña debe coincidir"
  )
  private String passwordConfirmation;

  private String phone;

  private String address;

  private String city;

}