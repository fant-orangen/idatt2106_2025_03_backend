package stud.ntnu.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.HouseholdCreateRequestDto;
import stud.ntnu.backend.repository.HouseholdRepository;
import stud.ntnu.backend.repository.UserRepository;
import stud.ntnu.backend.model.Household;
import stud.ntnu.backend.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing households. Handles creation, retrieval, updating, and deletion of
 * households.
 */
@Service
public class HouseholdService {

  private final HouseholdRepository householdRepository;
  private final UserRepository userRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param householdRepository repository for household operations
   * @param userRepository repository for user operations
   */
  public HouseholdService(HouseholdRepository householdRepository, UserRepository userRepository) {
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
  }

  /**
   * Retrieves all households.
   *
   * @return list of all households
   */
  public List<Household> getAllHouseholds() {
    return householdRepository.findAll();
  }

  /**
   * Retrieves a household by its ID.
   *
   * @param id the ID of the household
   * @return an Optional containing the household if found
   */
  public Optional<Household> getHouseholdById(Integer id) {
    return householdRepository.findById(id);
  }

  /**
   * Saves a household.
   *
   * @param household the household to save
   * @return the saved household
   */
  public Household saveHousehold(Household household) {
    return householdRepository.save(household);
  }

  /**
   * Deletes a household by its ID.
   *
   * @param id the ID of the household to delete
   */
  public void deleteHousehold(Integer id) {
    householdRepository.deleteById(id);
  }

  /**
   * Creates a new household for the current authenticated user.
   * Checks if the user already has a household before creating a new one.
   *
   * @param requestDto the household creation request
   * @return the created household
   * @throws IllegalStateException if the user already has a household
   */
  public Household createHousehold(HouseholdCreateRequestDto requestDto) {
    // Get the current authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    // Find the user by email
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user already has a household
    if (user.getHousehold() != null) {
      throw new IllegalStateException("User already has a household");
    }

    // Create a new household
    Household household = new Household(requestDto.getName(), requestDto.getAddress(), requestDto.getPopulationCount());

    // Save the household
    household = householdRepository.save(household);

    // Update the user with the new household
    user.setHousehold(household);
    userRepository.save(user);

    return household;
  }
}
