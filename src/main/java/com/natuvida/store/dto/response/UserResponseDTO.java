package com.natuvida.store.dto.response;

import com.natuvida.store.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserResponseDTO {
  private UUID id;
  private String name;
  private String lastName;
  private String email;
  private String phone;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Role role;
  private boolean enabled;
  private String address;
  private String city;
  private boolean customer;

}
