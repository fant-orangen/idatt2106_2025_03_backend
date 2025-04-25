package stud.ntnu.backend.controller;

import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.poi.PoiItemDto;
import stud.ntnu.backend.service.PoiService;
import stud.ntnu.backend.util.LocationUtil;

import java.util.List;

/**
 * Provides access to public Points of Interest (POIs).
 * Includes tilfluktsrom, hjertestartere, matstasjoner,
 * and similar resources, with map-based filtering support.
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */


@RestController
@RequestMapping("/api/poi")
public class PoiController {
    private final PoiService poiService;

    public PoiController(PoiService poiService) {
        this.poiService = poiService;
    }

    /**
     * Retrieves all public points of interest.
     *
     * @return a list of all public points of interest
     */
    @GetMapping("/public")
    public List<PoiItemDto> getPublicPointsOfInterest() {
        return poiService.getAllPointsOfInterest()
                .stream()
                .map(PoiItemDto::fromEntity)
                .toList();
    }
    /**
     * Retrieves all points of interest of a specific type.
     *
     * @return a list of all points of interest of a specific type
     */
    @GetMapping("/type/{id}")
    public List<PoiItemDto> getPointsOfInterestByTypeId(@PathVariable int id) {
        return poiService.getPointsOfInterestByTypeId(id)
                .stream()
                .map(PoiItemDto::fromEntity)
                .toList();
    }
    /**
     * Retrieves poi by id
     *
     * @param id the id of the poi
     * @return the poi with the given id as a PoiItemDto
     */
    @GetMapping("/{id}")
    public PoiItemDto getPointOfInterestById(@PathVariable int id) {
        return poiService.getPointOfInterestById(id)
                .map(PoiItemDto::fromEntity)
                .orElse(null);
    }

    /**
     * Retrieves all points of interest of a specific type within a given distance from a specified location.
     *
     * @param id        the ID of the point of interest type (optional)
     * @param latitude  the latitude of the location
     * @param longitude the longitude of the location
     * @param distance  the distance in meters
     * @return a list of points of interest within the specified distance
     */
    @GetMapping("/type/nearby")
    public List<PoiItemDto> getPointsOfInterestByTypeIdAndDistance(
            @RequestParam(required = false) Integer id,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double distance) {
        return (id == null ? poiService.getAllPointsOfInterest() : poiService.getPointsOfInterestByTypeId(id))
                .stream()
                .filter(poi -> LocationUtil.calculateDistance(latitude, longitude,
                        poi.getLatitude().doubleValue(), poi.getLongitude().doubleValue()) <= distance)
                .map(PoiItemDto::fromEntity)
                .toList();
    }


}
