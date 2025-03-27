package com.natuvida.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDTO {
  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 3, max = 50, message = "El nombre debe tener más de 2 caracteres")
  private String firstName;

  @NotBlank(message = "El apellido es obligatorio")
  @Size(min = 3, max = 50, message = "El apellido debe tener más de 2 caracteres")
  private String lastName;

  @NotBlank(message = "El número de teléfono es obligatorio")
  @Pattern(regexp = "^3[0-9]{9}$", message = "El número de teléfono debe empezar con 3 y tener 10 dígitos en total")
  private String phoneNumber;

  @Pattern(regexp = "^[0-9]{5,20}$", message = "Formato de ID nacional inválido")
  private String nationalId;

  @NotBlank(message = "La dirección es obligatoria")
  @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
  private String address;

  @Size(max = 50, message = "La ciudad no puede exceder 50 caracteres")
  private String city;
}