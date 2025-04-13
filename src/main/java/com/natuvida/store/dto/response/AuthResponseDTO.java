package com.natuvida.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDTO {
  private String token;
  private String refreshToken;
  private UserResponseDTO user;
  private String redirectUrl;
}
