package stud.ntnu.backend.service.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import stud.ntnu.backend.model.map.PointOfInterest;
import stud.ntnu.backend.model.map.PoiType;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.map.PointOfInterestRepository;
import stud.ntnu.backend.repository.map.PoiTypeRepository;
import stud.ntnu.backend.repository.user.UserRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service responsible for importing Points of Interest (POIs) from the Overpass API and saving them
 * into the application's database. Supports importing various types of POIs such as gas stations,
 * hospitals, shelters, and grocery stores.
 * <p>
 * This service is typically triggered at application startup to ensure that system-managed POIs are
 * available for use.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PoiImportService {

  /**
   * Repository for persisting and retrieving PointOfInterest entities.
   */
  private final PointOfInterestRepository poiRepository;

  /**
   * Repository for persisting and retrieving PoiType entities.
   */
  private final PoiTypeRepository poiTypeRepository;

  /**
   * Repository for persisting and retrieving User entities.
   */
  private final UserRepository userRepository;

  /**
   * The URL endpoint for the Overpass API.
   */
  private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";

  /**
   * Array of amenity types used for filtering POIs.
   */
  private static final String[] AMENITIES = {"hospital", "fuel", "shelter"};

  /**
   * Array of emergency types used for filtering POIs.
   */
  private static final String[] EMERGENCY = {"shelter"};

  /**
   * The email address of the admin user who will be set as the creator of imported POIs.
   */
  private static final String ADMIN_EMAIL = "admin@example.com"; //TODO: change this to a better admin email

  /**
   * Utility class to hold opening and closing times
   */
  @Data
  @AllArgsConstructor
  private static class OpeningHours {

    private final String openFrom;
    private final String openTo;
  }

  /**
   * Parses opening hours string from OpenStreetMap format to separate opening and closing times.
   * Only considers weekday (Monday-Friday) hours for simplicity.
   *
   * @param openingHoursStr OpenStreetMap opening hours string (e.g. "Mo-Fr 07:00-20:30, Sa
   *                        08:00-20:30")
   * @return OpeningHours object containing weekday opening and closing times, or null if no valid
   * times found
   */
  private OpeningHours parseOpeningHours(String openingHoursStr) {
    if (openingHoursStr == null || openingHoursStr.isEmpty()) {
      return null;
    }

    // Pattern to match "Mo-Fr HH:MM-HH:MM" format
    Pattern pattern = Pattern.compile("Mo-Fr\\s*(\\d{2}:\\d{2})-(\\d{2}:\\d{2})");
    Matcher matcher = pattern.matcher(openingHoursStr);

    if (matcher.find()) {
      String openFrom = matcher.group(1);
      String openTo = matcher.group(2);
      return new OpeningHours(openFrom, openTo);
    }

    return null;
  }

  /**
   * Imports gas stations from the Overpass API and saves them as POIs in the database. Only POIs
   * with amenity "fuel" are imported.
   */
  @Transactional
  public void importGasStationsFromOverpass() {
    importPois(
        "Gas Station",
        this::buildOverpassGasStationQuery,
        el -> {
          Map<String, String> tags = el.tags;
          String amenity = tags != null ? tags.get("amenity") : null;
          return "fuel".equals(amenity);
        },
        (el, type) -> {
          Map<String, String> tags = el.tags;
          return tags != null ? tags.getOrDefault("name", type) : type;
        }
    );
  }

  /**
   * Imports hospitals from the Overpass API and saves them as POIs in the database. Only POIs with
   * amenity "hospital" are imported.
   */
  @Transactional
  public void importHospitalsFromOverpass() {
    importPois(
        "Hospital",
        this::buildOverpassHospitalQuery,
        el -> {
          Map<String, String> tags = el.tags;
          String amenity = tags != null ? tags.get("amenity") : null;
          return "hospital".equals(amenity);
        },
        (el, type) -> {
          Map<String, String> tags = el.tags;
          return tags != null ? tags.getOrDefault("name", type) : type;
        }
    );
  }

  /**
   * Imports shelters from the Overpass API and saves them as POIs in the database. POIs with
   * amenity "shelter" or emergency "shelter" are imported.
   */
  @Transactional
  public void importSheltersFromOverpass() {
    importPois(
        "Shelter",
        this::buildOverpassShelterQuery,
        el -> {
          Map<String, String> tags = el.tags;
          String amenity = tags != null ? tags.get("amenity") : null;
          String emergency = tags != null ? tags.get("emergency") : null;
          return "shelter".equals(amenity) || "shelter".equals(emergency);
        },
        (el, type) -> {
          Map<String, String> tags = el.tags;
          return tags != null ? tags.getOrDefault("name", type) : type;
        }
    );
  }

  // TODO: Also import opening hours if possible

  /**
   * Imports grocery stores from the Overpass API and saves them as POIs in the database. POIs with
   * shop type "supermarket", "grocery", or "convenience" are imported.
   */
  @Transactional
  public void importGroceryStoresFromOverpass() {
    importPois(
        "Grocery Store",
        this::buildOverpassGroceryStoreQuery,
        el -> {
          Map<String, String> tags = el.tags;
          String shop = tags != null ? tags.get("shop") : null;
          return "supermarket".equals(shop) || "grocery".equals(shop) || "convenience".equals(shop);
        },
        (el, type) -> {
          Map<String, String> tags = el.tags;
          return tags != null ? tags.getOrDefault("name", type) : type;
        }
    );
  }

  /**
   * Imports police stations from the Overpass API and saves them as POIs in the database. Only POIs
   * with amenity "police" are imported.
   */
  @Transactional
  public void importPoliceStationsFromOverpass() {
    importPois(
        "Police Station",
        this::buildOverpassPoliceStationQuery,
        el -> {
          Map<String, String> tags = el.tags;
          String amenity = tags != null ? tags.get("amenity") : null;
          return "police".equals(amenity);
        },
        (el, type) -> {
          Map<String, String> tags = el.tags;
          return tags != null ? tags.getOrDefault("name", type) : type;
        }
    );
  }

  /**
   * Imports pharmacies from the Overpass API and saves them as POIs in the database. Includes POIs
   * with either amenity="pharmacy" or healthcare="pharmacy".
   */
  @Transactional
  public void importPharmaciesFromOverpass() {
    importPois(
        "Pharmacy",
        this::buildOverpassPharmacyQuery,
        el -> {
          Map<String, String> tags = el.tags;
          String amenity = tags != null ? tags.get("amenity") : null;
          String healthcare = tags != null ? tags.get("healthcare") : null;
          return "pharmacy".equals(amenity) || "pharmacy".equals(healthcare);
        },
        (el, type) -> {
          Map<String, String> tags = el.tags;
          return tags != null ? tags.getOrDefault("name", type) : type;
        }
    );
  }

  /**
   * Imports fire stations from the Overpass API and saves them as POIs in the database. Only POIs
   * with amenity "fire_station" are imported.
   */
  @Transactional
  public void importFireStationsFromOverpass() {
    importPois(
        "Fire Station",
        this::buildOverpassFireStationQuery,
        el -> {
          Map<String, String> tags = el.tags;
          String amenity = tags != null ? tags.get("amenity") : null;
          return "fire_station".equals(amenity);
        },
        (el, type) -> {
          Map<String, String> tags = el.tags;
          return tags != null ? tags.getOrDefault("name", type) : type;
        }
    );
  }

  /**
   * Placeholder for future use or for importing all POI types. Currently does nothing.
   */
  @Transactional
  public void importPoisFromOverpass() {
    // Intentionally left blank or for future multi-type import logic
  }

  /**
   * Utility method to import POIs from Overpass API.
   * <p>
   * This method fetches POIs from the Overpass API using a supplied query, filters the results,
   * extracts the name, and saves each POI to the database with the specified type and admin user.
   *
   * @param typeName      The name of the POI type (e.g., "Gas Station").
   * @param querySupplier Supplies the Overpass query string.
   * @param filter        Predicate to filter OverpassElement.
   * @param nameExtractor Function to extract the POI name from OverpassElement and type.
   */
  private void importPois(
      String typeName,
      QuerySupplier querySupplier,
      Predicate<OverpassElement> filter,
      NameExtractor nameExtractor
  ) {
    User adminUser = userRepository.findByEmail(ADMIN_EMAIL)
        .orElseThrow(() -> new IllegalStateException("Admin user not found"));
    PoiType poiType = poiTypeRepository.findAll().stream()
        .filter(pt -> pt.getName().equalsIgnoreCase(typeName))
        .findFirst()
        .orElseGet(() -> poiTypeRepository.save(new PoiType(typeName)));
    String query = querySupplier.get();
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.postForObject(OVERPASS_URL, "data=" + query, String.class);
    OverpassResponse overpassResponse = JsonUtil.fromJson(response, OverpassResponse.class);
    if (overpassResponse == null || overpassResponse.elements == null) {
      return;
    }
    for (OverpassElement el : overpassResponse.elements) {
      if (!filter.test(el)) {
        continue;
      }
      BigDecimal lat = el.lat != null ? BigDecimal.valueOf(el.lat)
          : (el.center != null ? BigDecimal.valueOf(el.center.lat) : null);
      BigDecimal lon = el.lon != null ? BigDecimal.valueOf(el.lon)
          : (el.center != null ? BigDecimal.valueOf(el.center.lon) : null);
      if (lat == null || lon == null) {
        continue;
      }
      String name = nameExtractor.extract(el, typeName);

      // Extract opening hours and address from tags
      Map<String, String> tags = el.tags;
      String openingHoursStr = tags != null ? tags.get("opening_hours") : null;

      // Build address from available address tags
      StringBuilder address = new StringBuilder();
      if (tags != null) {
        String street = tags.get("addr:street");
        String housenumber = tags.get("addr:housenumber");
        String postcode = tags.get("addr:postcode");
        String city = tags.get("addr:city");

        if (street != null) {
          address.append(street);
          if (housenumber != null) {
            address.append(" ").append(housenumber);
          }
        }
        if (postcode != null || city != null) {
          if (address.length() > 0) {
            address.append(", ");
          }
          if (postcode != null) {
            address.append(postcode).append(" ");
          }
          if (city != null) {
            address.append(city);
          }
        }
      }

      PointOfInterest poi = new PointOfInterest(poiType, name, lat, lon, adminUser);

      // Parse and set opening hours if available
      if (openingHoursStr != null) {
        OpeningHours hours = parseOpeningHours(openingHoursStr);
        if (hours != null) {
          poi.setOpenFrom(hours.getOpenFrom());
          poi.setOpenTo(hours.getOpenTo());
        }
      }

      if (address.length() > 0) {
        poi.setAddress(address.toString());
      }

      poiRepository.save(poi);
    }
  }

  /**
   * Functional interface for supplying Overpass query strings.
   */
  @FunctionalInterface
  private interface QuerySupplier {

    /**
     * Returns the Overpass query string.
     *
     * @return the query string
     */
    String get();
  }

  /**
   * Functional interface for extracting the name of a POI from an OverpassElement.
   */
  @FunctionalInterface
  private interface NameExtractor {

    /**
     * Extracts the name of the POI from the OverpassElement and type name.
     *
     * @param el       the OverpassElement
     * @param typeName the type name of the POI
     * @return the extracted name
     */
    String extract(OverpassElement el, String typeName);
  }

  /**
   * Builds the Overpass query string for gas stations in Norway.
   *
   * @return the Overpass query string for gas stations
   */
  private String buildOverpassGasStationQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"fuel\"](area);" +
        "way[\"amenity\"=\"fuel\"](area);" +
        ");out center tags;";
  }

  /**
   * Builds the Overpass query string for hospitals in Norway.
   *
   * @return the Overpass query string for hospitals
   */
  private String buildOverpassHospitalQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"hospital\"](area);" +
        "way[\"amenity\"=\"hospital\"](area);" +
        ");out center tags;";
  }

  /**
   * Builds the Overpass query string for shelters in Norway.
   *
   * @return the Overpass query string for shelters
   */
  private String buildOverpassShelterQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"shelter\"](area);" +
        "way[\"amenity\"=\"shelter\"](area);" +
        "node[\"emergency\"=\"shelter\"](area);" +
        "way[\"emergency\"=\"shelter\"](area);" +
        ");out center tags;";
  }

  /**
   * Builds the Overpass query string for grocery stores in Norway.
   *
   * @return the Overpass query string for grocery stores
   */
  private String buildOverpassGroceryStoreQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"shop\"=\"supermarket\"](area);" +
        "way[\"shop\"=\"supermarket\"](area);" +
        "node[\"shop\"=\"grocery\"](area);" +
        "way[\"shop\"=\"grocery\"](area);" +
        "node[\"shop\"=\"convenience\"](area);" +
        "way[\"shop\"=\"convenience\"](area);" +
        ");out center tags;";
  }

  /**
   * Builds the Overpass query string for police stations in Norway.
   *
   * @return the Overpass query string for police stations
   */
  private String buildOverpassPoliceStationQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"police\"](area);" +
        "way[\"amenity\"=\"police\"](area);" +
        ");out center tags;";
  }

  /**
   * Builds the Overpass query string for pharmacies in Norway. Includes both amenity=pharmacy and
   * healthcare=pharmacy tags.
   *
   * @return the Overpass query string for pharmacies
   */
  private String buildOverpassPharmacyQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"pharmacy\"](area);" +
        "way[\"amenity\"=\"pharmacy\"](area);" +
        "node[\"healthcare\"=\"pharmacy\"](area);" +
        "way[\"healthcare\"=\"pharmacy\"](area);" +
        ");out center tags;";
  }

  /**
   * Builds the Overpass query string for fire stations in Norway.
   *
   * @return the Overpass query string for fire stations
   */
  private String buildOverpassFireStationQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"fire_station\"](area);" +
        "way[\"amenity\"=\"fire_station\"](area);" +
        ");out center tags;";
  }

  /**
   * Represents the response from the Overpass API. Contains a list of OverpassElement objects.
   */
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OverpassResponse {

    /**
     * The list of elements returned by the Overpass API.
     */
    @JsonProperty("elements")
    private List<OverpassElement> elements;
  }

  /**
   * Represents an element in the Overpass API response. Can be a node or way, and may contain tags
   * and center coordinates.
   */
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OverpassElement {

    /**
     * The type of the element (e.g., "node", "way").
     */
    @JsonProperty("type")
    private String type;

    /**
     * The unique identifier of the element.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * The latitude of the element (if available).
     */
    @JsonProperty("lat")
    private Double lat;

    /**
     * The longitude of the element (if available).
     */
    @JsonProperty("lon")
    private Double lon;

    /**
     * The center coordinates of the element (for ways).
     */
    @JsonProperty("center")
    private Center center;

    /**
     * The tags associated with the element.
     */
    @JsonProperty("tags")
    private Map<String, String> tags;

    /**
     * Represents the center coordinates of a way element in the Overpass API response.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Center {

      /**
       * The latitude of the center.
       */
      @JsonProperty("lat")
      private Double lat;

      /**
       * The longitude of the center.
       */
      @JsonProperty("lon")
      private Double lon;
    }
  }

  /**
   * Simple JSON utility using Jackson for deserializing JSON strings.
   */
  static class JsonUtil {

    /**
     * The shared Jackson ObjectMapper instance.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Deserializes a JSON string into an object of the specified class.
     *
     * @param json  the JSON string
     * @param clazz the class to deserialize into
     * @param <T>   the type of the class
     * @return the deserialized object, or null if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
      try {
        return MAPPER.readValue(json, clazz);
      } catch (Exception e) {
        return null;
      }
    }
  }
}
