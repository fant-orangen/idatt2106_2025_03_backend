package stud.ntnu.backend.service.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.UserRepository;

import java.util.Collections;

/**
 * Custom implementation of UserDetailsService that loads user details from the database.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Load a user by username (email in our case).
   *
   * @param email the email of the user to load
   * @return UserDetails object containing the user's credentials and authorities
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    // Create a UserDetails object with the user's email, password hash, and role
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPasswordHash(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
    );
  }
}