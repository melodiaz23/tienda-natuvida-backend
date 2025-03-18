package com.natuvida.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String email;

  private String username;

  @Column(name = "created_at")
  @CreatedDate //  Automatically sets the field when the entity is first saved
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate // automatically updates the field when the entity is modified
  private LocalDateTime updatedAt;

  /*
   TODO: authentication fields later
   private String password;
   private boolean enabled = true;
   private Set<Role> roles = new HashSet<>();
  */

  // A User may or may not have a Customer profile
  //  Deleting a User will NOT delete the associated Customer.
  @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  private Customer customer;

  // Flag to indicate if this user is a customer
  @Column(name = "is_customer")
  private boolean isCustomer = false;



}
