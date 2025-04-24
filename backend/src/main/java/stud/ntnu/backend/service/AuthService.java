package stud.ntnu.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.AuthRequestDto;
import stud.ntnu.backend.dto.AuthResponseDto;
import stud.ntnu.backend.dto.RegisterRequestDto;
import stud.ntnu.backend.model.Role;
import stud.ntnu.backend.model.User;
import stud.ntnu.backend.repository.UserRepository;
import stud.ntnu.backend.util.JwtUtil;

/**
 * Service for handling authentication-related operations.
 */
@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
      UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Authenticate a user and generate a JWT token.
   *
   * @param authRequest the authentication request containing email and password
   * @return an AuthResponseDto containing the JWT token and user information
   * @throws BadCredentialsException if authentication fails
   */
  public AuthResponseDto login(AuthRequestDto authRequest) {
    // Authenticate the user
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
    );

    // Set the authentication in the security context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate JWT token
    String jwt = jwtUtil.generateToken(authRequest.getEmail());

    // Get user details to include in the response
    User user = userRepository.findByEmail(authRequest.getEmail())
        .orElseThrow(() -> new BadCredentialsException("User not found"));

    // Create and return the response
    return new AuthResponseDto(
        jwt,
        user.getId(),
        user.getEmail(),
        user.getRole().getName(),
        user.getHousehold() != null ? user.getHousehold().getId() : null
    );
  }

  /**
   * Register a new user with the USER role.
   *
   * @param registrationRequest the registration request containing email, password, name,
   *                           and optional home address and location coordinates
   * @throws IllegalArgumentException if a user with the given email already exists
   */
  public void register(RegisterRequestDto registrationRequest) {
    // Check if user already exists
    if (userRepository.existsByEmail(registrationRequest.getEmail())) {
      throw new IllegalArgumentException("User with this email already exists");
    }

    // Validation is now handled by annotations and @Valid in the controller

    // Create a new user with USER role (ID 1)
    Role userRole = new Role();
    userRole.setId(1); // USER role ID from data.sql

    // Create the user with hashed password
    User newUser = new User(
        registrationRequest.getEmail(),
        passwordEncoder.encode(registrationRequest.getPassword()),
        userRole
    );

    // Set additional fields
    newUser.setName(registrationRequest.getName());
    newUser.setHomeAddress(registrationRequest.getHomeAddress());
    newUser.setHomeLatitude(registrationRequest.getHomeLatitude());
    newUser.setHomeLongitude(registrationRequest.getHomeLongitude());

    // Save the user
    userRepository.save(newUser);
  }
}
