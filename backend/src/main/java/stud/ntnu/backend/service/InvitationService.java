package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.InvitationRepository;
import stud.ntnu.backend.model.Invitation;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing invitations. Handles creation, retrieval, updating, and deletion of
 * invitations.
 */
@Service
public class InvitationService {

  private final InvitationRepository invitationRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param invitationRepository repository for invitation operations
   */
  public InvitationService(InvitationRepository invitationRepository) {
    this.invitationRepository = invitationRepository;
  }

  /**
   * Retrieves all invitations.
   *
   * @return list of all invitations
   */
  public List<Invitation> getAllInvitations() {
    return invitationRepository.findAll();
  }

  /**
   * Retrieves an invitation by its ID.
   *
   * @param id the ID of the invitation
   * @return an Optional containing the invitation if found
   */
  public Optional<Invitation> getInvitationById(Integer id) {
    return invitationRepository.findById(id);
  }

  /**
   * Saves an invitation.
   *
   * @param invitation the invitation to save
   * @return the saved invitation
   */
  public Invitation saveInvitation(Invitation invitation) {
    return invitationRepository.save(invitation);
  }

  /**
   * Deletes an invitation by its ID.
   *
   * @param id the ID of the invitation to delete
   */
  public void deleteInvitation(Integer id) {
    invitationRepository.deleteById(id);
  }
}