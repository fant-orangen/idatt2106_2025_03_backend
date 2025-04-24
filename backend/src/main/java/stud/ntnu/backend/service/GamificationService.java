package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.GamificationActivityRepository;
import stud.ntnu.backend.repository.UserGamificationActivityRepository;
import stud.ntnu.backend.model.information.GamificationActivity;
import stud.ntnu.backend.model.user.UserGamificationActivity;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing gamification activities and user progress. Handles creation, retrieval,
 * updating, and deletion of gamification activities and user gamification activities.
 */
@Service
public class GamificationService {

  private final GamificationActivityRepository gamificationActivityRepository;
  private final UserGamificationActivityRepository userGamificationActivityRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param gamificationActivityRepository     repository for gamification activity operations
   * @param userGamificationActivityRepository repository for user gamification activity operations
   */
  public GamificationService(GamificationActivityRepository gamificationActivityRepository,
      UserGamificationActivityRepository userGamificationActivityRepository) {
    this.gamificationActivityRepository = gamificationActivityRepository;
    this.userGamificationActivityRepository = userGamificationActivityRepository;
  }

  /**
   * Retrieves all gamification activities.
   *
   * @return list of all gamification activities
   */
  public List<GamificationActivity> getAllActivities() {
    return gamificationActivityRepository.findAll();
  }

  /**
   * Retrieves a gamification activity by its ID.
   *
   * @param id the ID of the gamification activity
   * @return an Optional containing the gamification activity if found
   */
  public Optional<GamificationActivity> getActivityById(Integer id) {
    return gamificationActivityRepository.findById(id);
  }

  /**
   * Saves a gamification activity.
   *
   * @param activity the gamification activity to save
   * @return the saved gamification activity
   */
  public GamificationActivity saveActivity(GamificationActivity activity) {
    return gamificationActivityRepository.save(activity);
  }

  /**
   * Retrieves all user gamification activities.
   *
   * @return list of all user gamification activities
   */
  public List<UserGamificationActivity> getAllUserActivities() {
    return userGamificationActivityRepository.findAll();
  }

  /**
   * Retrieves a user gamification activity by its ID.
   *
   * @param id the ID of the user gamification activity
   * @return an Optional containing the user gamification activity if found
   */
  public Optional<UserGamificationActivity> getUserActivityById(Integer id) {
    return userGamificationActivityRepository.findById(id);
  }

  /**
   * Saves a user gamification activity.
   *
   * @param userActivity the user gamification activity to save
   * @return the saved user gamification activity
   */
  public UserGamificationActivity saveUserActivity(UserGamificationActivity userActivity) {
    return userGamificationActivityRepository.save(userActivity);
  }
}