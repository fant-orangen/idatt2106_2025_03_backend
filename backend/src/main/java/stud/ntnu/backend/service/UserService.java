package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.UserRepository;
import stud.ntnu.backend.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing users. Handles retrieval, updating, and deletion of users. Note: User
 * creation is handled by AuthService.
 */
@Service
public class UserService {

  private final UserRepository userRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param userRepository repository for user operations
   */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user
   * @return an Optional containing the user if found
   */
  public Optional<User> getUserById(Integer id) {
    return userRepository.findById(id);
  }

  /**
   * Retrieves a user by their email.
   *
   * @param email the email of the user
   * @return an Optional containing the user if found
   */
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * Saves a user.
   *
   * @param user the user to save
   * @return the saved user
   */
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  /**
   * Deletes a user by their ID.
   *
   * @param id the ID of the user to delete
   */
  public void deleteUser(Integer id) {
    userRepository.deleteById(id);
  }
}