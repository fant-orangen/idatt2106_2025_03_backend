package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.GroupRepository;
import stud.ntnu.backend.model.Group;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing groups. Handles creation, retrieval, updating, and deletion of groups.
 */
@Service
public class GroupService {

  private final GroupRepository groupRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param groupRepository repository for group operations
   */
  public GroupService(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  /**
   * Retrieves all groups.
   *
   * @return list of all groups
   */
  public List<Group> getAllGroups() {
    return groupRepository.findAll();
  }

  /**
   * Retrieves a group by its ID.
   *
   * @param id the ID of the group
   * @return an Optional containing the group if found
   */
  public Optional<Group> getGroupById(Integer id) {
    return groupRepository.findById(id);
  }

  /**
   * Saves a group.
   *
   * @param group the group to save
   * @return the saved group
   */
  public Group saveGroup(Group group) {
    return groupRepository.save(group);
  }

  /**
   * Deletes a group by its ID.
   *
   * @param id the ID of the group to delete
   */
  public void deleteGroup(Integer id) {
    groupRepository.deleteById(id);
  }
}