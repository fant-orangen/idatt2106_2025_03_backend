package stud.ntnu.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.AuthRequestDto;
import stud.ntnu.backend.dto.AuthResponseDto;
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

  public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
      UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
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
}