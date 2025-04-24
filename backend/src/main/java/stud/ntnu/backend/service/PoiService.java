package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.repository.PoiTypeRepository;
import stud.ntnu.backend.repository.PointOfInterestRepository;
import stud.ntnu.backend.model.PoiType;
import stud.ntnu.backend.model.PointOfInterest;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing points of interest and POI types. Handles creation, retrieval, updating, and
 * deletion of POIs and POI types.
 */
@Service
public class PoiService {

  private final PointOfInterestRepository pointOfInterestRepository;
  private final PoiTypeRepository poiTypeRepository;

  /**
   * Constructor for dependency injection.
   *
   * @param pointOfInterestRepository repository for point of interest operations
   * @param poiTypeRepository         repository for POI type operations
   */
  public PoiService(PointOfInterestRepository pointOfInterestRepository,
      PoiTypeRepository poiTypeRepository) {
    this.pointOfInterestRepository = pointOfInterestRepository;
    this.poiTypeRepository = poiTypeRepository;
  }

  /**
   * Retrieves all points of interest.
   *
   * @return list of all points of interest
   */
  public List<PointOfInterest> getAllPointsOfInterest() {
    return pointOfInterestRepository.findAll();
  }

  /**
   * Retrieves a point of interest by its ID.
   *
   * @param id the ID of the point of interest
   * @return an Optional containing the point of interest if found
   */
  public Optional<PointOfInterest> getPointOfInterestById(Integer id) {
    return pointOfInterestRepository.findById(id);
  }

  /**
   * Saves a point of interest.
   *
   * @param poi the point of interest to save
   * @return the saved point of interest
   */
  public PointOfInterest savePointOfInterest(PointOfInterest poi) {
    return pointOfInterestRepository.save(poi);
  }

  /**
   * Deletes a point of interest by its ID.
   *
   * @param id the ID of the point of interest to delete
   */
  public void deletePointOfInterest(Integer id) {
    pointOfInterestRepository.deleteById(id);
  }

  /**
   * Retrieves all POI types.
   *
   * @return list of all POI types
   */
  public List<PoiType> getAllPoiTypes() {
    return poiTypeRepository.findAll();
  }

  /**
   * Retrieves a POI type by its ID.
   *
   * @param id the ID of the POI type
   * @return an Optional containing the POI type if found
   */
  public Optional<PoiType> getPoiTypeById(Integer id) {
    return poiTypeRepository.findById(id);
  }

  /**
   * Saves a POI type.
   *
   * @param poiType the POI type to save
   * @return the saved POI type
   */
  public PoiType savePoiType(PoiType poiType) {
    return poiTypeRepository.save(poiType);
  }

  /**
   * Deletes a POI type by its ID.
   *
   * @param id the ID of the POI type to delete
   */
  public void deletePoiType(Integer id) {
    poiTypeRepository.deleteById(id);
  }
}