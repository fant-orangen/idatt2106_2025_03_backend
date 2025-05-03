package stud.ntnu.backend.service.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PoiImportService {

  private final PointOfInterestRepository poiRepository;
  private final PoiTypeRepository poiTypeRepository;
  private final UserRepository userRepository;

  private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";
  private static final String[] AMENITIES = {"hospital", "fuel", "shelter"};
  private static final String[] EMERGENCY = {"shelter"};
  private static final String ADMIN_EMAIL = "admin@example.com"; //TODO: change this to a better admin email

  @Transactional
  public void importGasStationsFromOverpass() {
    User adminUser = userRepository.findByEmail(ADMIN_EMAIL)
        .orElseThrow(() -> new IllegalStateException("Admin user not found"));
    Map<String, PoiType> poiTypeCache = new HashMap<>();
    String type = "Gas Station";
    PoiType poiType = poiTypeRepository.findAll().stream()
        .filter(pt -> pt.getName().equalsIgnoreCase(type))
        .findFirst()
        .orElseGet(() -> poiTypeRepository.save(new PoiType(type)));
    poiTypeCache.put(type, poiType);
    String query = buildOverpassGasStationQuery();
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.postForObject(OVERPASS_URL, "data=" + query, String.class);
    OverpassResponse overpassResponse = JsonUtil.fromJson(response, OverpassResponse.class);
    if (overpassResponse == null || overpassResponse.elements == null) {
      return;
    }
    for (OverpassElement el : overpassResponse.elements) {
      Map<String, String> tags = el.tags;
      String amenity = tags != null ? tags.get("amenity") : null;
      if (!"fuel".equals(amenity)) {
        continue;
      }
      BigDecimal lat = el.lat != null ? BigDecimal.valueOf(el.lat)
          : (el.center != null ? BigDecimal.valueOf(el.center.lat) : null);
      BigDecimal lon = el.lon != null ? BigDecimal.valueOf(el.lon)
          : (el.center != null ? BigDecimal.valueOf(el.center.lon) : null);
      if (lat == null || lon == null) {
        continue;
      }
      String name = tags != null ? tags.getOrDefault("name", type) : type;
      PointOfInterest poi = new PointOfInterest(poiType, name, lat, lon, adminUser);
      poiRepository.save(poi);
    }
  }

  @Transactional
  public void importHospitalsFromOverpass() {
    User adminUser = userRepository.findByEmail(ADMIN_EMAIL)
        .orElseThrow(() -> new IllegalStateException("Admin user not found"));
    String type = "Hospital";
    PoiType poiType = poiTypeRepository.findAll().stream()
        .filter(pt -> pt.getName().equalsIgnoreCase(type))
        .findFirst()
        .orElseGet(() -> poiTypeRepository.save(new PoiType(type)));
    String query = buildOverpassHospitalQuery();
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.postForObject(OVERPASS_URL, "data=" + query, String.class);
    OverpassResponse overpassResponse = JsonUtil.fromJson(response, OverpassResponse.class);
    if (overpassResponse == null || overpassResponse.elements == null) {
      return;
    }
    for (OverpassElement el : overpassResponse.elements) {
      Map<String, String> tags = el.tags;
      String amenity = tags != null ? tags.get("amenity") : null;
      if (!"hospital".equals(amenity)) {
        continue;
      }
      BigDecimal lat = el.lat != null ? BigDecimal.valueOf(el.lat)
          : (el.center != null ? BigDecimal.valueOf(el.center.lat) : null);
      BigDecimal lon = el.lon != null ? BigDecimal.valueOf(el.lon)
          : (el.center != null ? BigDecimal.valueOf(el.center.lon) : null);
      if (lat == null || lon == null) {
        continue;
      }
      String name = tags != null ? tags.getOrDefault("name", type) : type;
      PointOfInterest poi = new PointOfInterest(poiType, name, lat, lon, adminUser);
      poiRepository.save(poi);
    }
  }

  @Transactional
  public void importSheltersFromOverpass() {
    User adminUser = userRepository.findByEmail(ADMIN_EMAIL)
        .orElseThrow(() -> new IllegalStateException("Admin user not found"));
    String type = "Shelter";
    PoiType poiType = poiTypeRepository.findAll().stream()
        .filter(pt -> pt.getName().equalsIgnoreCase(type))
        .findFirst()
        .orElseGet(() -> poiTypeRepository.save(new PoiType(type)));
    String query = buildOverpassShelterQuery();
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.postForObject(OVERPASS_URL, "data=" + query, String.class);
    OverpassResponse overpassResponse = JsonUtil.fromJson(response, OverpassResponse.class);
    if (overpassResponse == null || overpassResponse.elements == null) {
      return;
    }
    for (OverpassElement el : overpassResponse.elements) {
      Map<String, String> tags = el.tags;
      String amenity = tags != null ? tags.get("amenity") : null;
      String emergency = tags != null ? tags.get("emergency") : null;
      if (!"shelter".equals(amenity) && !"shelter".equals(emergency)) {
        continue;
      }
      BigDecimal lat = el.lat != null ? BigDecimal.valueOf(el.lat)
          : (el.center != null ? BigDecimal.valueOf(el.center.lat) : null);
      BigDecimal lon = el.lon != null ? BigDecimal.valueOf(el.lon)
          : (el.center != null ? BigDecimal.valueOf(el.center.lon) : null);
      if (lat == null || lon == null) {
        continue;
      }
      String name = tags != null ? tags.getOrDefault("name", type) : type;
      PointOfInterest poi = new PointOfInterest(poiType, name, lat, lon, adminUser);
      poiRepository.save(poi);
    }
  }

  @Transactional
  public void importGroceryStoresFromOverpass() {
    User adminUser = userRepository.findByEmail(ADMIN_EMAIL)
        .orElseThrow(() -> new IllegalStateException("Admin user not found"));
    String type = "Grocery Store";
    PoiType poiType = poiTypeRepository.findAll().stream()
        .filter(pt -> pt.getName().equalsIgnoreCase(type))
        .findFirst()
        .orElseGet(() -> poiTypeRepository.save(new PoiType(type)));
    String query = buildOverpassGroceryStoreQuery();
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.postForObject(OVERPASS_URL, "data=" + query, String.class);
    OverpassResponse overpassResponse = JsonUtil.fromJson(response, OverpassResponse.class);
    if (overpassResponse == null || overpassResponse.elements == null) {
      return;
    }
    for (OverpassElement el : overpassResponse.elements) {
      Map<String, String> tags = el.tags;
      String shop = tags != null ? tags.get("shop") : null;
      if (!"supermarket".equals(shop) && !"grocery".equals(shop) && !"convenience".equals(shop)) {
        continue;
      }
      BigDecimal lat = el.lat != null ? BigDecimal.valueOf(el.lat)
          : (el.center != null ? BigDecimal.valueOf(el.center.lat) : null);
      BigDecimal lon = el.lon != null ? BigDecimal.valueOf(el.lon)
          : (el.center != null ? BigDecimal.valueOf(el.center.lon) : null);
      if (lat == null || lon == null) {
        continue;
      }
      String name = tags != null ? tags.getOrDefault("name", type) : type;
      PointOfInterest poi = new PointOfInterest(poiType, name, lat, lon, adminUser);
      poiRepository.save(poi);
    }
  }

  // Placeholder for future use or for all POI types
  @Transactional
  public void importPoisFromOverpass() {
    // Intentionally left blank or for future multi-type import logic
  }

  private String buildOverpassGasStationQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"fuel\"](area);" +
        "way[\"amenity\"=\"fuel\"](area);" +
        ");out center tags;";
  }

  private String buildOverpassHospitalQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"hospital\"](area);" +
        "way[\"amenity\"=\"hospital\"](area);" +
        ");out center tags;";
  }

  private String buildOverpassShelterQuery() {
    return "[out:json][timeout:180];" +
        "area[\"ISO3166-1\"=\"NO\"][admin_level=2];(" +
        "node[\"amenity\"=\"shelter\"](area);" +
        "way[\"amenity\"=\"shelter\"](area);" +
        "node[\"emergency\"=\"shelter\"](area);" +
        "way[\"emergency\"=\"shelter\"](area);" +
        ");out center tags;";
  }

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

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OverpassResponse {

    @JsonProperty("elements")
    private List<OverpassElement> elements;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OverpassElement {

    @JsonProperty("type")
    private String type;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("lat")
    private Double lat;
    @JsonProperty("lon")
    private Double lon;
    @JsonProperty("center")
    private Center center;
    @JsonProperty("tags")
    private Map<String, String> tags;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Center {

      @JsonProperty("lat")
      private Double lat;
      @JsonProperty("lon")
      private Double lon;
    }
  }

  // Simple JSON utility using Jackson
  static class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) {
      try {
        return MAPPER.readValue(json, clazz);
      } catch (Exception e) {
        return null;
      }
    }
  }
}
