package com.natuvida.store.service;

import com.natuvida.store.dto.request.CustomerRequestDTO;
import com.natuvida.store.dto.response.CustomerResponseDTO;
import com.natuvida.store.dto.response.UserResponseDTO;
import com.natuvida.store.entity.Customer;
import com.natuvida.store.mapper.CustomerMapper;
import com.natuvida.store.mapper.UserMapper;
import com.natuvida.store.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

  @Transactional(readOnly = true)
  public CustomerResponseDTO findCustomerByUserEmail(String email) {
    Optional<Customer> customer = customerRepository.findByUserEmail(email);
    return customer.map(customerMapper::toDto).orElse(null);
  }

  @Transactional
  public CustomerResponseDTO createCustomer(CustomerRequestDTO request, String email) {
    Customer customer = new Customer();

    // Set fields from the request DTO
    customer.setFirstName(request.getFirstName());
    customer.setLastName(request.getLastName());
    customer.setPhoneNumber(request.getPhoneNumber());
    customer.setNationalId(request.getNationalId());
    customer.setAddress(request.getAddress());
    customer.setCity(request.getCity());

    // Try to associate with existing user if email provided
    if (StringUtils.hasText(email)) {
      UserResponseDTO user = userService.findByEmail(email);
      if (user != null) {
        // Verificar si el usuario ya tiene un customer asociado
        Optional<Customer> existingCustomer = customerRepository.findByUserId(user.getId());
        if (existingCustomer.isPresent()) {
          // Actualizar el customer existente en lugar de crear uno nuevo
          Customer customerToUpdate = existingCustomer.get();
          customerMapper.updateCustomerFromDTO(request, customerToUpdate);
          return customerMapper.toDto(customerRepository.save(customerToUpdate));
        } else {
          // Asociar con el usuario existente
          customer.setUser(userMapper.toEntity(user));
          // Only use user's address as fallback if address wasn't provided in the request
          if (user.getAddress() != null && !StringUtils.hasText(request.getAddress())) {
            customer.setAddress(user.getAddress());
            customer.setCity(user.getCity());
          }
        }
      }
    }
    return customerMapper.toDto(customerRepository.save(customer));
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