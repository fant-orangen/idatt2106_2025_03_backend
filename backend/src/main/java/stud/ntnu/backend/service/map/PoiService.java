package stud.ntnu.backend.service.map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.backend.dto.poi.CreatePoiDto;
import stud.ntnu.backend.dto.poi.PoiPreviewDto;
import stud.ntnu.backend.dto.poi.UpdatePoiDto;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.PoiTypeRepository;
import stud.ntnu.backend.repository.map.PointOfInterestRepository;
import stud.ntnu.backend.model.map.PoiType;
import stud.ntnu.backend.model.map.PointOfInterest;

import java.util.List;
import java.util.Optional;

import static stud.ntnu.backend.util.LocationUtil.calculateDistance;

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

  //Constants
  String poiNotFound = "POI type not found";

  /**
   * Retrieves all points of interest.
   *
   * @return list of all points of interest
   */
  public List<PointOfInterest> getAllPointsOfInterest() {
    return pointOfInterestRepository.findAll();
  }

    /**
     * Retrieves a preview of all points of interest with pagination.
     *
     * @param pageable the pagination information
     * @return a paginated list of points of interest previews
     */
  @Transactional(readOnly = true)
  public Page<PoiPreviewDto> getPoiPreviews(Pageable pageable) {
    // Fetch paginated POIs
    Page<PointOfInterest> poiPage = pointOfInterestRepository.findAll(pageable);

    // Map entities to PoiPreviewDto
    return poiPage.map(poi -> new PoiPreviewDto(
            poi.getId(),
            poi.getName(),
            poi.getPoiType().getName()
    ));
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
   * Retrieves all points of interest of a specific type.
   *
   * @param typeId the ID of the POI type
   * @return list of points of interest of the specified type
   */
  public List<PointOfInterest> getPointsOfInterestByTypeId(Integer typeId) {
    return pointOfInterestRepository.findByPoiTypeId(typeId);
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

  public static PointOfInterest findNearestPoi(double latitude, double longitude,
      List<PointOfInterest> pois) {
    return pois.stream()
        .min((poi1, poi2) -> Double.compare(
            calculateDistance(latitude, longitude,
                poi1.getLatitude().doubleValue(), poi1.getLongitude().doubleValue()),
            calculateDistance(latitude, longitude,
                poi2.getLatitude().doubleValue(), poi2.getLongitude().doubleValue())))
        .orElse(null);
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

  //JavaDoc for createPointOfInterest method

  /**
   * Creates a new point of interest.
   *
   * @param createPoiDto the DTO containing point of interest information
   * @param currentUser  the user creating the point of interest
   * @return the created point of interest
   */
  @Transactional
  public PointOfInterest createPointOfInterest(CreatePoiDto createPoiDto, User currentUser) {
    // Get the POI type
    PoiType poiType = getPoiTypeById(createPoiDto.getPoiTypeId())
        .orElseThrow(() -> new IllegalStateException(poiNotFound));

    // Create a new PointOfInterest entity
    PointOfInterest poi = new PointOfInterest(
        poiType,
        createPoiDto.getName(),
        createPoiDto.getLatitude(),
        createPoiDto.getLongitude(),
        currentUser
    );

    // Set optional fields if provided
    poi.setDescription(createPoiDto.getDescription());
    poi.setAddress(createPoiDto.getAddress());
    poi.setOpenFrom(createPoiDto.getOpenFrom());
    poi.setOpenTo(createPoiDto.getOpenTo());
    poi.setContactInfo(createPoiDto.getContactInfo());

    // Save and return the POI
    return savePointOfInterest(poi);
  }

  /**
   * Updates an existing point of interest.
   *
   * @param id           the ID of the point of interest to update
   * @param updatePoiDto the DTO containing updated point of interest information
   * @return the updated point of interest
   */
  @Transactional
  public PointOfInterest updatePointOfInterest(Integer id, UpdatePoiDto updatePoiDto) {
    // First check if the point of interest exists
    if (!pointOfInterestRepository.existsById(id)) {
      throw new IllegalStateException("Point of interest not found with ID: " + id);
    }

    // Option 1: Use the direct repository update method if all fields are provided
    if (updatePoiDto.getName() != null &&
        updatePoiDto.getLatitude() != null &&
        updatePoiDto.getLongitude() != null &&
        updatePoiDto.getDescription() != null &&
        updatePoiDto.getOpenFrom() != null &&
        updatePoiDto.getOpenTo() != null &&
        updatePoiDto.getContactInfo() != null &&
        updatePoiDto.getPoiTypeId() != null) {

      PoiType poiType = poiTypeRepository.findById(updatePoiDto.getPoiTypeId())
          .orElseThrow(() -> new IllegalStateException(poiNotFound));

      pointOfInterestRepository.updatePointOfInterest(
          id,
          updatePoiDto.getName(),
          updatePoiDto.getLatitude(),
          updatePoiDto.getLongitude(),
          updatePoiDto.getDescription(),
          updatePoiDto.getOpenFrom(),
          updatePoiDto.getOpenTo(),
          updatePoiDto.getContactInfo(),
          poiType
      );

      // Return the updated entity
      return pointOfInterestRepository.findById(id).orElseThrow();
    }

    // Option 2: For partial updates, use the traditional approach
    PointOfInterest poi = pointOfInterestRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Point of interest not found with ID: " + id));

    // Update fields if provided
    if (updatePoiDto.getName() != null) {
      poi.setName(updatePoiDto.getName());
    }
    if (updatePoiDto.getLatitude() != null) {
      poi.setLatitude(updatePoiDto.getLatitude());
    }
    if (updatePoiDto.getLongitude() != null) {
      poi.setLongitude(updatePoiDto.getLongitude());
    }
    if (updatePoiDto.getDescription() != null) {
      poi.setDescription(updatePoiDto.getDescription());
    }
    if (updatePoiDto.getOpenFrom() != null) {
      poi.setOpenFrom(updatePoiDto.getOpenFrom());
    }
    if (updatePoiDto.getOpenTo() != null) {
      poi.setOpenTo(updatePoiDto.getOpenTo());
    }
    if (updatePoiDto.getContactInfo() != null) {
      poi.setContactInfo(updatePoiDto.getContactInfo());
    }
    if (updatePoiDto.getPoiTypeId() != null) {
      PoiType poiType = poiTypeRepository.findById(updatePoiDto.getPoiTypeId())
          .orElseThrow(() -> new IllegalStateException(poiNotFound));
      poi.setPoiType(poiType);
    }

    // Save directly using the repository
    return pointOfInterestRepository.save(poi);
  }
}