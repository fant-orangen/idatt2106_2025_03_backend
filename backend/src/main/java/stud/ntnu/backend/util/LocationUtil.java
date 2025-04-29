package stud.ntnu.backend.util;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;

import java.math.BigDecimal;
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
    //using openstreetmap to go from address to coordinates
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
}