package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.ReflectionRepository;
import stud.ntnu.backend.model.Reflection;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing reflections. Handles creation, retrieval, updating, and deletion of
 * reflections.
 */
@Service
public class ReflectionService {

  private final ReflectionRepository reflectionRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param reflectionRepository repository for reflection operations
   */
  public ReflectionService(ReflectionRepository reflectionRepository) {
    this.reflectionRepository = reflectionRepository;
  }

  /**
   * Retrieves all reflections.
   *
   * @return list of all reflections
   */
  public List<Reflection> getAllReflections() {
    return reflectionRepository.findAll();
  }

  /**
   * Retrieves a reflection by its ID.
   *
   * @param id the ID of the reflection
   * @return an Optional containing the reflection if found
   */
  public Optional<Reflection> getReflectionById(Integer id) {
    return reflectionRepository.findById(id);
  }

  /**
   * Saves a reflection.
   *
   * @param reflection the reflection to save
   * @return the saved reflection
   */
  public Reflection saveReflection(Reflection reflection) {
    return reflectionRepository.save(reflection);
  }

  /**
   * Deletes a reflection by its ID.
   *
   * @param id the ID of the reflection to delete
   */
  public void deleteReflection(Integer id) {
    reflectionRepository.deleteById(id);
  }
}