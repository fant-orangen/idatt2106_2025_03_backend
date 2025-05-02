package stud.ntnu.backend.controller.user;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;
import stud.ntnu.backend.util.LocationUtil;


/**
 * Controller for handling address-related requests. Provides endpoints to get coordinates by
 * address and vice versa.
 */
@RestController
@RequestMapping("/api/address")
public class AddressController {


  /**
   * Retrieves coordinates based on the provided address.
   *
   * @param address the address to get coordinates for
   * @return ResponseEntity containing the coordinates or an error message
   */
  @GetMapping("/coordinates-by-address")
  public ResponseEntity<?> getCoordinatesByAddress(@RequestParam String address) {
    try {
      CoordinatesItemDto coordinates = LocationUtil.getCoordinatesByAddress(address);
      return ResponseEntity.ok(coordinates);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid address: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An error occurred while fetching coordinates.");
    }
  }

  /**
   * Retrieves the address based on the provided coordinates.
   *
   * @param latitude  the latitude of the coordinates
   * @param longitude the longitude of the coordinates
   * @return ResponseEntity containing the address or an error message
   */
  @GetMapping("/address-by-coordinates")
  public ResponseEntity<?> getAddressByCoordinates(@RequestParam String latitude,
      @RequestParam String longitude) {
    try {
      String address = LocationUtil.getAddressByCords(new CoordinatesItemDto(latitude, longitude));
      return ResponseEntity.ok(address);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid coordinates: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An error occurred while fetching address.");
    }
  }

}
