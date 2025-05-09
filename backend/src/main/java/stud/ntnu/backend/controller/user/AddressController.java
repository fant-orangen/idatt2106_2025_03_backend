package stud.ntnu.backend.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;
import stud.ntnu.backend.util.LocationUtil;

/**
 * Controller for handling address-related requests. Provides endpoints to get coordinates by
 * address and vice versa.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Address Management", description = "Operations for converting between addresses and coordinates")
public class AddressController {

  /**
   * Retrieves coordinates based on the provided address. User endpoint:
   * /api/user/coordinates-by-address
   *
   * @param address the address to get coordinates for
   * @return ResponseEntity containing the coordinates or an error message
   */
  @Operation(summary = "Get coordinates by address", description = "Retrieves coordinates based on the provided address.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved coordinates", 
          content = @Content(schema = @Schema(implementation = CoordinatesItemDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid address", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/user/coordinates-by-address")
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
   * Retrieves the address based on the provided coordinates. User endpoint:
   * /api/user/address-by-coordinates
   *
   * @param latitude  the latitude of the coordinates
   * @param longitude the longitude of the coordinates
   * @return ResponseEntity containing the address or an error message
   */
  @Operation(summary = "Get address by coordinates", description = "Retrieves the address based on the provided coordinates.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved address", 
          content = @Content(schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid coordinates", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/user/address-by-coordinates")
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
