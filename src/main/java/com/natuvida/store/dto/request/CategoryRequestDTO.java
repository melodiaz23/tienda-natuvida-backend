package com.natuvida.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDTO {
  @NotBlank(message = "El nombre de la categoría es obligatorio")
  @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
  private String name;

  @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
  private String description;
}