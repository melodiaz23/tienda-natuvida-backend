package com.natuvida.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDTO {
  private String code;
  private String message;
  private LocalDateTime timestamp;

}