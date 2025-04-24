package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.CrisisEventRepository;
import stud.ntnu.backend.model.CrisisEvent;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing crisis events. Handles creation, retrieval, updating, and deletion of crisis
 * events.
 */
@Service
public class CrisisEventService {

  private final CrisisEventRepository crisisEventRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param crisisEventRepository repository for crisis event operations
   */
  public CrisisEventService(CrisisEventRepository crisisEventRepository) {
    this.crisisEventRepository = crisisEventRepository;
  }

  /**
   * Retrieves all crisis events.
   *
   * @return list of all crisis events
   */
  public List<CrisisEvent> getAllCrisisEvents() {
    return crisisEventRepository.findAll();
  }

  /**
   * Retrieves a crisis event by its ID.
   *
   * @param id the ID of the crisis event
   * @return an Optional containing the crisis event if found
   */
  public Optional<CrisisEvent> getCrisisEventById(Integer id) {
    return crisisEventRepository.findById(id);
  }

  /**
   * Saves a crisis event.
   *
   * @param crisisEvent the crisis event to save
   * @return the saved crisis event
   */
  public CrisisEvent saveCrisisEvent(CrisisEvent crisisEvent) {
    return crisisEventRepository.save(crisisEvent);
  }

  /**
   * Deletes a crisis event by its ID.
   *
   * @param id the ID of the crisis event to delete
   */
  public void deleteCrisisEvent(Integer id) {
    crisisEventRepository.deleteById(id);
  }
}