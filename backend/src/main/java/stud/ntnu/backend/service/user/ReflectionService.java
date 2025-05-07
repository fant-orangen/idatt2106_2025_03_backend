package stud.ntnu.backend.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.group.GroupSummaryDto;
import stud.ntnu.backend.dto.user.CreateReflectionDto;
import stud.ntnu.backend.dto.user.ReflectionResponseDto;
import stud.ntnu.backend.dto.user.UpdateReflectionDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.Reflection;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.CrisisEventRepository;
import stud.ntnu.backend.repository.user.ReflectionRepository;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.service.group.GroupService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing reflections. Handles creation, retrieval, updating, and deletion of
 * reflections.
 */
@Service
public class ReflectionService {

  private final ReflectionRepository reflectionRepository;
  private final UserRepository userRepository;
  private final CrisisEventRepository crisisEventRepository;
  private final GroupService groupService;

  /**
   * Constructor for dependency injection.
   *
   * @param reflectionRepository repository for reflection operations
   * @param userRepository repository for user operations
   * @param crisisEventRepository repository for crisis event operations
   * @param groupService service for group operations
   */
  public ReflectionService(ReflectionRepository reflectionRepository, UserRepository userRepository,
                          CrisisEventRepository crisisEventRepository, GroupService groupService) {
    this.reflectionRepository = reflectionRepository;
    this.userRepository = userRepository;
    this.crisisEventRepository = crisisEventRepository;
    this.groupService = groupService;
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
   * Retrieves all reflections for a specific user.
   *
   * @param userId the ID of the user
   * @param pageable pagination information
   * @return a page of reflections
   */
  public Page<ReflectionResponseDto> getReflectionsByUserId(Integer userId, Pageable pageable) {
    return reflectionRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
        .map(ReflectionResponseDto::fromEntity);
  }

  /**
   * Retrieves all shared reflections for users in a specific household.
   *
   * @param householdId the ID of the household
   * @param pageable pagination information
   * @return a page of shared reflections
   */
  public Page<ReflectionResponseDto> getSharedReflectionsByHouseholdId(Integer householdId, Pageable pageable) {
    return reflectionRepository.findSharedByHouseholdId(householdId, pageable)
        .map(ReflectionResponseDto::fromEntity);
  }

  /**
   * Retrieves all shared reflections for users in households that are members of a specific group.
   *
   * @param groupId the ID of the group
   * @param pageable pagination information
   * @return a page of shared reflections
   */
  public Page<ReflectionResponseDto> getSharedReflectionsByGroupId(Integer groupId, Pageable pageable) {
    return reflectionRepository.findSharedByGroupId(groupId, pageable)
        .map(ReflectionResponseDto::fromEntity);
  }

  /**
   * Retrieves all shared reflections from all groups the user's household is a member of.
   *
   * @param email the email of the user
   * @param pageable pagination information
   * @return a page of shared reflections from all groups
   */
  public Page<ReflectionResponseDto> getSharedReflectionsFromAllUserGroups(String email, Pageable pageable) {
    // Get all groups the user's household is a member of
    Page<GroupSummaryDto> userGroups = groupService.getCurrentUserGroups(email, pageable);

    if (userGroups.isEmpty()) {
      // Return empty page if user is not a member of any group
      return Page.empty(pageable);
    }

    // Get reflections from all groups and combine them
    List<ReflectionResponseDto> allReflections = userGroups.getContent().stream()
        .flatMap(group -> reflectionRepository.findSharedByGroupId(group.getId(), Pageable.unpaged())
            .map(ReflectionResponseDto::fromEntity)
            .getContent().stream())
        .distinct() // Remove duplicates
        .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt())) // Sort by createdAt DESC
        .collect(Collectors.toList());

    // Convert to Page
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), allReflections.size());

    // Handle case where start is beyond list size
    if (start >= allReflections.size()) {
      return Page.empty(pageable);
    }

    return new PageImpl<>(allReflections.subList(start, end), pageable, allReflections.size());
  }

  /**
   * Retrieves all shared reflections that are visible to a specific user (from their household and groups).
   *
   * @param userId the ID of the user
   * @param pageable pagination information
   * @return a page of shared reflections
   */
  public Page<ReflectionResponseDto> getSharedReflectionsVisibleToUser(Integer userId, Pageable pageable) {
    return reflectionRepository.findSharedVisibleToUser(userId, pageable)
        .map(ReflectionResponseDto::fromEntity);
  }

  /**
   * Creates a new reflection for a user.
   *
   * @param userId the ID of the user
   * @param createDto the DTO containing reflection information
   * @return the created reflection
   * @throws IllegalArgumentException if the user is not found
   */
  @Transactional
  public ReflectionResponseDto createReflection(Integer userId, CreateReflectionDto createDto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    Reflection reflection = new Reflection();
    reflection.setUser(user);
    reflection.setContent(createDto.getContent());
    reflection.setShared(createDto.getShared());

    // Set crisis event if provided
    if (createDto.getCrisisEventId() != null) {
      CrisisEvent crisisEvent = crisisEventRepository.findById(createDto.getCrisisEventId())
          .orElseThrow(() -> new IllegalArgumentException("Crisis event not found with ID: " + createDto.getCrisisEventId()));
      reflection.setCrisisEvent(crisisEvent);
    }

    Reflection savedReflection = reflectionRepository.save(reflection);
    return ReflectionResponseDto.fromEntity(savedReflection);
  }

  /**
   * Updates an existing reflection.
   *
   * @param id the ID of the reflection to update
   * @param userId the ID of the user who owns the reflection
   * @param updateDto the DTO containing updated reflection information
   * @return the updated reflection
   * @throws IllegalArgumentException if the reflection is not found or does not belong to the user
   */
  @Transactional
  public ReflectionResponseDto updateReflection(Integer id, Integer userId, UpdateReflectionDto updateDto) {
    Reflection reflection = reflectionRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Reflection not found with ID: " + id));

    // Check if the reflection belongs to the user
    if (!reflection.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("Reflection does not belong to the user");
    }

    reflection.setContent(updateDto.getContent());
    reflection.setShared(updateDto.getShared());

    // Update crisis event if provided
    if (updateDto.getCrisisEventId() != null) {
      CrisisEvent crisisEvent = crisisEventRepository.findById(updateDto.getCrisisEventId())
          .orElseThrow(() -> new IllegalArgumentException("Crisis event not found with ID: " + updateDto.getCrisisEventId()));
      reflection.setCrisisEvent(crisisEvent);
    } else if (updateDto.getCrisisEventId() == null && reflection.getCrisisEvent() != null) {
      // If crisis event ID is explicitly set to null, remove the association
      reflection.setCrisisEvent(null);
    }

    Reflection updatedReflection = reflectionRepository.save(reflection);
    return ReflectionResponseDto.fromEntity(updatedReflection);
  }

  /**
   * Soft deletes a reflection by its ID if it belongs to the specified user.
   *
   * @param id the ID of the reflection to delete
   * @param userId the ID of the user who owns the reflection
   * @throws IllegalArgumentException if the reflection is not found or does not belong to the user
   */
  @Transactional
  public void deleteReflection(Integer id, Integer userId) {
    Reflection reflection = reflectionRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Reflection not found with ID: " + id));

    // Check if the reflection belongs to the user
    if (!reflection.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("Reflection does not belong to the user");
    }

    // Soft delete by setting the deleted flag to true
    reflection.setDeleted(true);
    reflectionRepository.save(reflection);
  }
}