package com.natuvida.store.entity;

import com.natuvida.store.exception.ValidationException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
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

  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @NonNull
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NonNull
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @NonNull
  @Column(nullable = false, name = "phone_number", unique = true)
  private String phoneNumber;

  @Column(name = "national_id", unique = true, length = 20)
  private String nationalId;

  @NonNull
  @Column(nullable = false, length = 255)
  private String address;

  @NonNull
  @Column(length = 50)
  private String city;

  @OneToMany(mappedBy = "customer") // Lazy by default
  private List<Order> orders = new ArrayList<>(); // Prevent null pointer exceptions

//   Set up automatic date/time tracking -  Spring Data JPA's Auditing
  @Column(name = "created_at")
  @CreatedDate //  Automatically sets the field when the entity is first saved
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate // automatically updates the field when the entity is modified
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private Boolean enabled = true;

  public void setUser(User user) {
    if (user != null && user.getCustomer() != null && !user.getCustomer().equals(this)) {
      throw new ValidationException("Este usuario ya está asociado a otro cliente");
    }

    User oldUser = this.user;
    this.user = user;

    // Desasociar el usuario anterior, evita circular references
    if (oldUser != null && oldUser.getCustomer() == this) {
      oldUser.setCustomer(null);
    }

    if (user != null && user.getCustomer() != this) {
      user.setCustomer(this);
    }
  }
}
