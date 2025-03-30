package com.natuvida.store.security;

import com.natuvida.store.entity.User;
import com.natuvida.store.enums.Role;
import com.natuvida.store.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;
  private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    // Extract user details from OAuth provider
    Map<String, Object> attributes = oAuth2User.getAttributes();
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");

    // Find or create user in our database
    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createNewUser(email, name));

    // Get user authority/role
    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    if (user.getRole() != null) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    // Return a DefaultOAuth2User with our authorities
    return new DefaultOAuth2User(
        authorities,
        attributes,
        "email" // The key used to extract the username
    );
  }

  private User createNewUser(String email, String name) {
    User newUser = new User();
    newUser.setEmail(email);
    newUser.setUsername(email); // Using email as username
    newUser.setEnabled(true);

    // Set default role
    newUser.setRole(Role.USER);

    return userRepository.save(newUser);
  }
}