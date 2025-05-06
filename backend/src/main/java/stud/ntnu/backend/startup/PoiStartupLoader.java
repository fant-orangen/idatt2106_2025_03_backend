package stud.ntnu.backend.startup;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import stud.ntnu.backend.service.map.PoiImportService;

/**
 * Loads Points of Interest (POIs) from Overpass API into the database
 * when the application is ready. This ensures that the system-managed
 * POIs (such as hospitals, gas stations, and shelters) are available
 * for use immediately after startup.
 */
@Component
@RequiredArgsConstructor
public class PoiStartupLoader {

  /**
   * Service responsible for importing POIs from Overpass API.
   */
  private final PoiImportService poiImportService;

  /**
   * Event listener that triggers after the Spring Boot application is fully started.
   * Invokes the import of POIs from Overpass API.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    poiImportService.importGasStationsFromOverpass();
    poiImportService.importHospitalsFromOverpass();
    poiImportService.importSheltersFromOverpass();
    poiImportService.importGroceryStoresFromOverpass();
  }
}
