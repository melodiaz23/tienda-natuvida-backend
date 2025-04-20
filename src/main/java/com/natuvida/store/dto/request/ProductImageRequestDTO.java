package com.natuvida.store.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import java.util.UUID;

@Getter
@Setter
public class ProductImageRequestDTO {

  private UUID id; // null para imágenes nuevas, con valor para actualizar/referenciar existentes

  @NotBlank(message = "La URL de la imagen no puede estar vacía")
  @URL(message = "Debe proporcionar una URL válida")
  private String imageUrl; // O podría ser un ID temporal si manejas subida de archivos
  private String altText;

  private Integer displayOrder; // Orden en que se mostrará

  private Boolean isPrimary;

}