package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.map.MeetingPlaceRepository;
import stud.ntnu.backend.model.map.MeetingPlace;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing meeting places. Handles creation, retrieval, updating, and deletion of
 * meeting places.
 */
@Service
public class MeetingPlaceService {

  private final MeetingPlaceRepository meetingPlaceRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param meetingPlaceRepository repository for meeting place operations
   */
  public MeetingPlaceService(MeetingPlaceRepository meetingPlaceRepository) {
    this.meetingPlaceRepository = meetingPlaceRepository;
  }

  /**
   * Retrieves all meeting places.
   *
   * @return list of all meeting places
   */
  public List<MeetingPlace> getAllMeetingPlaces() {
    return meetingPlaceRepository.findAll();
  }

  /**
   * Retrieves a meeting place by its ID.
   *
   * @param id the ID of the meeting place
   * @return an Optional containing the meeting place if found
   */
  public Optional<MeetingPlace> getMeetingPlaceById(Integer id) {
    return meetingPlaceRepository.findById(id);
  }

  /**
   * Saves a meeting place.
   *
   * @param meetingPlace the meeting place to save
   * @return the saved meeting place
   */
  public MeetingPlace saveMeetingPlace(MeetingPlace meetingPlace) {
    return meetingPlaceRepository.save(meetingPlace);
  }

  /**
   * Deletes a meeting place by its ID.
   *
   * @param id the ID of the meeting place to delete
   */
  public void deleteMeetingPlace(Integer id) {
    meetingPlaceRepository.deleteById(id);
  }
}