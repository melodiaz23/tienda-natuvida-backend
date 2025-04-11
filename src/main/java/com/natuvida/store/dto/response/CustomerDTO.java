package com.natuvida.store.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CustomerDTO {
  private UUID id;
  private UUID userId;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String nationalId;
  private String address;
  private String city;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String userEmail;
}