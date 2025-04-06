package com.natuvida.store.dto.response;

import com.natuvida.store.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {
  private UUID id;
  private String email;
  private String username;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Role role;
  private boolean enabled;
  private boolean customer;

}
