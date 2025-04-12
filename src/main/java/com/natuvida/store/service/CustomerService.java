package com.natuvida.store.service;

import com.natuvida.store.dto.response.CustomerResponseDTO;
import com.natuvida.store.dto.response.UserResponseDTO;
import com.natuvida.store.entity.Customer;
import com.natuvida.store.mapper.CustomerMapper;
import com.natuvida.store.mapper.UserMapper;
import com.natuvida.store.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;
  private final UserMapper userMapper;
  private final UserService userService;

  public List<Customer> findAll() {
    return customerRepository.findAll();
  }

  public Optional<Customer> findById(UUID id) {
    return customerRepository.findById(id);
  }

  public Optional<Customer> findByUserId(UUID userId) {
    return customerRepository.findByUserId(userId);
  }

  @Transactional
  public CustomerResponseDTO createCustomer(String email, String firstName, String lastName, String phoneNumber, String nationalId, String address, String city) {
    Customer customer = new Customer();
    // Si se proporciona un userId, intentamos asociar el usuario
    // Si se proporciona un email, intentamos asociar el usuario
    if (email != null) {
      UserResponseDTO user = userService.findByEmail(email);
      if (user != null) {
        // Asociamos el usuario al customer
        customer.setUser(userMapper.toEntity(user));
        // Si no se proporciona dirección, usamos la del usuario
        if (address == null && user.getAddress() != null) {
          customer.setAddress(user.getAddress());
          customer.setCity(user.getCity());
        }
      }
    }
    customer.setFirstName(firstName);
    customer.setLastName(lastName);
    customer.setPhoneNumber(phoneNumber);
    customer.setNationalId(nationalId);

    if (address != null) {
      customer.setAddress(address);
      customer.setCity(city);
    }

    Customer savedCustomer = customerRepository.save(customer);
    return customerMapper.toDto(savedCustomer);
  }

  @Transactional
  public Customer updateCustomer(Customer customer) {
    return customerRepository.save(customer);
  }

  @Transactional
  public void deleteCustomer(UUID id) {
    customerRepository.deleteById(id);
  }

  public boolean existsByNationalId(String nationalId) {
    return customerRepository.existsByNationalId(nationalId);
  }
}