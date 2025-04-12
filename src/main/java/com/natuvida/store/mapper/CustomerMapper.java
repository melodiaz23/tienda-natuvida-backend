package com.natuvida.store.mapper;

import com.natuvida.store.dto.request.CustomerRequestDTO;
import com.natuvida.store.dto.response.CustomerResponseDTO;
import com.natuvida.store.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userEmail", source = "user.email")
  CustomerResponseDTO toDto(Customer entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "orders", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Customer toEntity(CustomerRequestDTO requestDTO);

  // Update existing entity from request
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "orders", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateCustomerFromDTO(CustomerRequestDTO requestDTO, @MappingTarget Customer customer);

  List<CustomerResponseDTO> toDtoList(List<Customer> customers);

  List<Customer> toEntityList(List<CustomerRequestDTO> customerRequestDTOs);

}
