package com.natuvida.store.entity;

import com.natuvida.store.enums.Role;
import com.natuvida.store.exception.ValidationException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String name;

  private String lastName;

  private String phone;

  @Column(unique = true, nullable = false)
  private String email;


  @Column(name = "created_at")
  @CreatedDate //  Automatically sets the field when the entity is first saved
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate // automatically updates the field when the entity is modified
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String password;

  @Column(nullable = false)
  private Boolean enabled = true;

  private String address;

  private String city;

  // Optional: indicate if address has been provided
  private Boolean hasAddressInfo = false;

  // A User may or may not have a Customer profile
  //  Deleting a User will NOT delete the associated Customer.
  @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  private Customer customer;

  public boolean isCustomer() {
    return customer != null;
  }

  public void setCustomer(Customer customer) {
    // No modificar relaciones existentes automáticamente
    if (customer != null && customer.getUser() != null && !customer.getUser().equals(this)) {
      throw new ValidationException("Este cliente ya está asociado a otro usuario");
    }

    Customer oldCustomer = this.customer;
    this.customer = customer;

    if (oldCustomer != null && oldCustomer.getUser() == this) {
      oldCustomer.setUser(null);
    }

    if (customer != null && customer.getUser() != this) {
      customer.setUser(this);
    }
  }

  // Methods from UserDetails interface
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();
    if (role != null) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    return authorities;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

}
