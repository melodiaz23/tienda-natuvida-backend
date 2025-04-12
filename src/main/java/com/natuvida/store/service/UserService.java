package com.natuvida.store.service;

import com.natuvida.store.dto.request.UserProfileRequestDTO;
import com.natuvida.store.dto.request.UserRequestDTO;
import com.natuvida.store.dto.response.UserResponseDTO;
import com.natuvida.store.entity.User;
import com.natuvida.store.enums.Role;
import com.natuvida.store.mapper.UserMapper;
import com.natuvida.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Transactional
  public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
    User user = new User();
    user.setEmail(requestDTO.getEmail());
    user.setName(requestDTO.getName());
    user.setLastName(requestDTO.getLastName());
    user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
    user.setEnabled(true);
    // Asignar el rol USER directamente
    user.setRole(Role.USER);

    User savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
  }

  @Transactional
  public UserResponseDTO updateUser(UUID userId, UserProfileRequestDTO profileDTO) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    if (profileDTO.getName() != null) {
      user.setName(profileDTO.getName());
    }

    if (profileDTO.getLastName() != null) {
      user.setLastName(profileDTO.getLastName());
    }

    if (profileDTO.getEmail() != null) {
      // Verificar que el email no esté ya en uso por otro usuario
      Optional<User> existingUser = userRepository.findByEmail(profileDTO.getEmail());
      if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
        throw new RuntimeException("El correo electrónico ya está en uso");
      }
      user.setEmail(profileDTO.getEmail());
    }

    if(profileDTO.getPhone() != null){
      user.setPhone(profileDTO.getPhone());
    }
    // Solo actualizar la contraseña si se proporciona una nueva
    if (profileDTO.getPassword() != null && !profileDTO.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(profileDTO.getPassword()));
    }

    // Actualizar la información de dirección
    if (profileDTO.getAddress() != null) {
      user.setAddress(profileDTO.getAddress());
      user.setHasAddressInfo(true);
    }

    if (profileDTO.getCity() != null) {
      user.setCity(profileDTO.getCity());
    }

    userMapper.updateUserFromProfileDto(profileDTO, user);
    User updatedUser = userRepository.save(user);
    return userMapper.toDto(updatedUser);
  }

  @Transactional(readOnly = true)
  public UUID getUserIdByEmail(String username) {
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return user.getId();
  }

  @Transactional(readOnly = true)
  public UserResponseDTO findByEmail(String email){
    Optional<User> userOp = userRepository.findByEmail(email);
    User user = null;
    if (userOp.isPresent()) user = userOp.get(); 
    return userMapper.toDto(user);

  }
}