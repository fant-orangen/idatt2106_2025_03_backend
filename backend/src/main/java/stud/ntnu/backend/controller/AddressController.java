package stud.ntnu.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.map.CoordinatesItemDto;
import stud.ntnu.backend.util.LocationUtil;

@RestController
@RequestMapping("/api/address")
public class AddressController {


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

}
