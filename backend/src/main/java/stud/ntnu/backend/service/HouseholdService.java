package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.HouseholdRepository;
import stud.ntnu.backend.model.Household;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing households. Handles creation, retrieval, updating, and deletion of
 * households.
 */
@Service
public class HouseholdService {

  private final HouseholdRepository householdRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param householdRepository repository for household operations
   */
  public HouseholdService(HouseholdRepository householdRepository) {
    this.householdRepository = householdRepository;
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
}