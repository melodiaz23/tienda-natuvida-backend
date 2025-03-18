package com.natuvida.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "customers")
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @NonNull
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NonNull
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @NonNull
  @Column(nullable = false, name = "phone_number")
  private String phoneNumber;

  @Column(name = "national_id", unique = true, length = 20)
  private String nationalId;

  @NonNull
  @Column(nullable = false, length = 255)
  private String address;

  @Column(length = 50)
  private String city;

  @OneToMany(mappedBy = "customer") // Lazy by default
  private List<Order> orders = new ArrayList<>(); // Prevent null pointer exceptions

  @Column(name = "email", unique = true)
  private String email;

//   Set up automatic date/time tracking -  Spring Data JPA's Auditing
  @Column(name = "created_at")
  @CreatedDate //  Automatically sets the field when the entity is first saved
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate // automatically updates the field when the entity is modified
  private LocalDateTime updatedAt;

  @Transient // Marks a field or method as not being persisted to the database
  public String getFullName() {
    return firstName + " " + lastName;
  }

}
