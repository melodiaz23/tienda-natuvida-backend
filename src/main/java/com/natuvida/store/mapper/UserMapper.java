package com.natuvida.store.mapper;

import com.natuvida.store.dto.request.UserRequestDTO;
import com.natuvida.store.dto.response.UserDTO;
import com.natuvida.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "isCustomer", expression = "java(entity.isCustomer())")
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
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "enabled", constant = "true") // New users are enabled by default
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "accountNonExpired", ignore = true)
  @Mapping(target = "accountNonLocked", ignore = true)
  @Mapping(target = "credentialsNonExpired", ignore = true)
  User toEntity(UserRequestDTO requestDTO);

  // Update existing user
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "accountNonExpired", ignore = true)
  @Mapping(target = "accountNonLocked", ignore = true)
  @Mapping(target = "credentialsNonExpired", ignore = true)
  void updateUserFromDto(UserDTO dto, @MappingTarget User user);

  List<UserDTO> toDtoList(List<User> users);
}