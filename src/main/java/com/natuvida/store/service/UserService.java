package com.natuvida.store.service;

import com.natuvida.store.dto.request.UserRequestDTO;
import com.natuvida.store.dto.response.UserDTO;
import com.natuvida.store.entity.User;
import com.natuvida.store.enums.Role;
import com.natuvida.store.mapper.UserMapper;
import com.natuvida.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Transactional
  public UserDTO registerUser(UserRequestDTO requestDTO) {
    User user = new User();
    user.setEmail(requestDTO.getEmail());
    user.setUsername(requestDTO.getEmail());
    user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
    user.setEnabled(true);

    // Asignar el rol USER directamente
    user.setRole(Role.USER);

    User savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
  }
}