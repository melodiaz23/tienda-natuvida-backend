package com.natuvida.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)

public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "national_id", unique = true, length = 20)
  private String nationalId;

  @Column(length = 255)
  private String address;

  @Column(length = 50)
  private String city;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "email", unique = true)
  private String email;

//   Set up automatic date/time tracking
  @Column(name = "created_at")
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

}
