package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.group.GroupInventoryContributionRepository;
import stud.ntnu.backend.model.group.GroupInventoryContribution;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing group inventory contributions. Handles creation, retrieval, updating, and
 * deletion of group inventory contributions.
 */
@Service
public class GroupInventoryService {

  private final GroupInventoryContributionRepository groupInventoryContributionRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param groupInventoryContributionRepository repository for group inventory contribution
   *                                             operations
   */
  public GroupInventoryService(
      GroupInventoryContributionRepository groupInventoryContributionRepository) {
    this.groupInventoryContributionRepository = groupInventoryContributionRepository;
  }

  /**
   * Retrieves all group inventory contributions.
   *
   * @return list of all group inventory contributions
   */
  public List<GroupInventoryContribution> getAllContributions() {
    return groupInventoryContributionRepository.findAll();
  }

  /**
   * Retrieves a group inventory contribution by its ID.
   *
   * @param id the ID of the group inventory contribution
   * @return an Optional containing the group inventory contribution if found
   */
  public Optional<GroupInventoryContribution> getContributionById(Integer id) {
    return groupInventoryContributionRepository.findById(id);
  }

  /**
   * Saves a group inventory contribution.
   *
   * @param contribution the group inventory contribution to save
   * @return the saved group inventory contribution
   */
  public GroupInventoryContribution saveContribution(GroupInventoryContribution contribution) {
    return groupInventoryContributionRepository.save(contribution);
  }

  /**
   * Deletes a group inventory contribution by its ID.
   *
   * @param id the ID of the group inventory contribution to delete
   */
  public void deleteContribution(Integer id) {
    groupInventoryContributionRepository.deleteById(id);
  }
}