package com.natuvida.store.mapper;

import com.natuvida.store.dto.request.UserProfileRequestDTO;
import com.natuvida.store.dto.request.UserRequestDTO;
import com.natuvida.store.dto.response.UserDTO;
import com.natuvida.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "customer", expression = "java(entity.isCustomer())")
  @Mapping(target = "role", source = "role")
  @Mapping(target = "address", source = "address") // Añadir mapeo para dirección
  @Mapping(target = "city", source = "city")
  UserDTO toDto(User entity);

  // Creating new users
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "accountNonExpired", ignore = true)
  @Mapping(target = "accountNonLocked", ignore = true)
  @Mapping(target = "credentialsNonExpired", ignore = true)
  User toEntity(UserDTO dto);

  // For creating new users
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "enabled", constant = "true") // New users are enabled by default
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "accountNonExpired", ignore = true)
  @Mapping(target = "accountNonLocked", ignore = true)
  @Mapping(target = "credentialsNonExpired", ignore = true)
  User toEntity(UserRequestDTO requestDTO);

  // Update existing user
  // Update user from profile request DTO
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "accountNonExpired", ignore = true)
  @Mapping(target = "accountNonLocked", ignore = true)
  @Mapping(target = "credentialsNonExpired", ignore = true)
//  @Mapping(target = "address", source = "address") // Añadir mapeo para dirección
//  @Mapping(target = "city", source = "city")
//  @Mapping(target = "phone", source = "phone")
  void updateUserFromProfileDto(UserProfileRequestDTO profileDto, @MappingTarget User user);

  List<UserDTO> toDtoList(List<User> users);
}