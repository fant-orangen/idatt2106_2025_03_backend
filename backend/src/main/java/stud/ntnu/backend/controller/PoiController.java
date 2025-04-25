package stud.ntnu.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.poi.PoiItemDto;
import stud.ntnu.backend.model.map.PointOfInterest;
import stud.ntnu.backend.service.PoiService;

import java.util.List;

/**
 * Provides access to public Points of Interest (POIs).
 * Includes tilfluktsrom, hjertestartere, matstasjoner,
 * and similar resources, with map-based filtering support.
 *
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

}
