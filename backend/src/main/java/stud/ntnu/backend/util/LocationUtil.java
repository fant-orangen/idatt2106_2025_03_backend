package stud.ntnu.backend.util;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;
import stud.ntnu.backend.model.map.CrisisEvent;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.service.user.UserService;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class LocationUtil {

  private LocationUtil() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Calculates the distance between two geographical points using the Haversine formula.
   *
   * @param lat1 latitude of the first point
   * @param lon1 longitude of the first point
   * @param lat2 latitude of the second point
   * @param lon2 longitude of the second point
   * @return distance in meters
   */
  public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int EARTH_RADIUS = 6371000; // in meters
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return EARTH_RADIUS * c;
  }

  /**
   * Retrieves the coordinates corresponding to the given address using OpenStreetMap's Nominatim
   * API.
   *
   * @param address the address for which to retrieve coordinates
   * @return a CoordinatesItemDto containing latitude and longitude
   */
  public static CoordinatesItemDto getCoordinatesByAddress(String address) {
    final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    try {
      // Build the URI with query parameters
      URI uri = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
          .queryParam("q", address)
          .queryParam("format", "json")
          .queryParam("addressdetails", 1)
          .build()
          .toUri();

      // Use RestTemplate to make the HTTP GET request
      RestTemplate restTemplate = new RestTemplate();
      List<Map<String, Object>> response = restTemplate.getForObject(uri, List.class);

      // Check if a result is returned
      if (response != null && !response.isEmpty()) {
        Map<String, Object> firstResult = response.get(0);
        String lat = (String) firstResult.get("lat");
        String lon = (String) firstResult.get("lon");

        return new CoordinatesItemDto(lat, lon);
      } else {
        throw new IllegalArgumentException("No coordinates found for the given address.");
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Error while fetching coordinates: " + e.getMessage(), e);
    }
  }

  /**
   * Retrieves the address corresponding to the given coordinates using OpenStreetMap's Nominatim
   * API.
   *
   * @param coordinates the coordinates for which to retrieve the address
   * @return a formatted address string
   */
  public static String getAddressByCords(CoordinatesItemDto coordinates) {
    final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse";

    try {
      // Build the URI with query parameters
      URI uri = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
          .queryParam("lat", coordinates.getLatitude())
          .queryParam("lon", coordinates.getLongitude())
          .queryParam("format", "json")
          .build()
          .toUri();

      // Use RestTemplate to make the HTTP GET request
      RestTemplate restTemplate = new RestTemplate();
      Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

      // Check if a result is returned
      if (response != null) {
        String displayName = (String) response.get("display_name");
        if (displayName != null) {
          // Split the display_name by commas
          String[] parts = displayName.split(",\\s*");
          // Reconstruct the desired format
          if (parts.length >= 8) {
            return String.join(", ", parts[0], parts[1], parts[5], parts[6], parts[7]);
          }
        }
        throw new IllegalArgumentException("Unexpected address format.");
      } else {
        throw new IllegalArgumentException("No address found for the given coordinates.");
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Error while fetching address: " + e.getMessage(), e);
    }
  }

  /**
   * Finds all users within a specified radius of a crisis event.
   *
   * @param userService the user service to get all users
   * @param latitude    the latitude of the crisis event
   * @param longitude   the longitude of the crisis event
   * @param radiusInKm  the radius in kilometers
   * @return list of users within the radius
   */
  public static List<User> findUsersWithinRadius(UserService userService, double latitude,
      double longitude, double radiusInKm) {
    List<User> allUsers = userService.getAllUsers();
    return allUsers.stream()
        .filter(user -> {
          // Check if user's home coordinates are within radius
          boolean userInRadius =
              user.getHomeLatitude() != null && user.getHomeLongitude() != null &&
                  calculateDistance(latitude, longitude,
                      user.getHomeLatitude().doubleValue(),
                      user.getHomeLongitude().doubleValue()) <= (radiusInKm
                      * 1000); // Convert km to meters

          // Check if user's household coordinates are within radius
          boolean householdInRadius = user.getHousehold() != null &&
              user.getHousehold().getLatitude() != null &&
              user.getHousehold().getLongitude() != null &&
              calculateDistance(latitude, longitude,
                  user.getHousehold().getLatitude().doubleValue(),
                  user.getHousehold().getLongitude().doubleValue()) <= (radiusInKm
                  * 1000); // Convert km to meters

          return userInRadius || householdInRadius;
        })
        .toList();
  }

  /**
   * Checks if a crisis event is within a specified distance of a user's home or household
   * location.
   *
   * @param user         the user to check
   * @param crisisEvent  the crisis event
   * @param distanceInKm the distance in kilometers
   * @return true if the crisis event is within the specified distance of the user's home or
   * household location
   */
  public static boolean isCrisisEventNearUser(User user, CrisisEvent crisisEvent,
      double distanceInKm) {
    if (crisisEvent == null || crisisEvent.getEpicenterLatitude() == null
        || crisisEvent.getEpicenterLongitude() == null) {
      return false;
    }

    double crisisLatitude = crisisEvent.getEpicenterLatitude().doubleValue();
    double crisisLongitude = crisisEvent.getEpicenterLongitude().doubleValue();

    // Check if user's home coordinates are within distance
    boolean userHomeNearby = user.getHomeLatitude() != null && user.getHomeLongitude() != null &&
        isWithinDistance(crisisLatitude, crisisLongitude,
            user.getHomeLatitude().doubleValue(),
            user.getHomeLongitude().doubleValue(),
            distanceInKm);

    // Check if user's household coordinates are within distance
    boolean householdNearby = user.getHousehold() != null &&
        user.getHousehold().getLatitude() != null &&
        user.getHousehold().getLongitude() != null &&
        isWithinDistance(crisisLatitude, crisisLongitude,
            user.getHousehold().getLatitude().doubleValue(),
            user.getHousehold().getLongitude().doubleValue(),
            distanceInKm);

    return userHomeNearby || householdNearby;
  }

  /**
   * Checks if two geographical points are within a specified distance of each other.
   *
   * @param lat1         latitude of the first point
   * @param lon1         longitude of the first point
   * @param lat2         latitude of the second point
   * @param lon2         longitude of the second point
   * @param distanceInKm the distance in kilometers
   * @return true if the points are within the specified distance of each other
   */
  public static boolean isWithinDistance(double lat1, double lon1, double lat2, double lon2,
      double distanceInKm) {
    return calculateDistance(lat1, lon1, lat2, lon2) <= (distanceInKm
        * 1000); // Convert km to meters
  }
}