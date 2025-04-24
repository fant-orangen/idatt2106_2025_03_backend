package stud.ntnu.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.InventoryItemCreateDto;
import stud.ntnu.backend.dto.InventoryItemDto;
import stud.ntnu.backend.dto.InventoryItemUpdateDto;
import stud.ntnu.backend.dto.InventoryStatusDto;
import stud.ntnu.backend.service.InventoryService;

import java.util.List;

/**
 * Handles inventory management at the household level.
 * Includes listing, adding, editing, or removing stock items,
 * tracking expiration dates, and computing preparedness grade.
 *
 * Based on Visjonsdokument 2025 for Krisefikser.no.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final Logger log = LoggerFactory.getLogger(InventoryController.class);

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Gets the inventory items for the authenticated user's household.
     *
     * @return ResponseEntity with the inventory items if successful, or an error message if the
     * user is not found or doesn't have a household
     */
    @GetMapping
    public ResponseEntity<?> getInventory() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            List<InventoryItemDto> inventoryItems = inventoryService.getHouseholdInventory(email);
            return ResponseEntity.ok(inventoryItems);
        } catch (IllegalStateException e) {
            log.info("Get inventory failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Adds a new inventory item to the authenticated user's household.
     *
     * @param createDto the inventory item creation DTO
     * @return ResponseEntity with the created inventory item if successful, or an error message if the
     * user is not found, doesn't have a household, or the product is not found
     */
    @PostMapping
    public ResponseEntity<?> createInventoryItem(@Valid @RequestBody InventoryItemCreateDto createDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            InventoryItemDto inventoryItem = inventoryService.createInventoryItem(email, createDto);
            return ResponseEntity.ok(inventoryItem);
        } catch (IllegalStateException e) {
            log.info("Create inventory item failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates an inventory item in the authenticated user's household.
     *
     * @param id the ID of the inventory item
     * @param updateDto the inventory item update DTO
     * @return ResponseEntity with the updated inventory item if successful, or an error message if the
     * user is not found, doesn't have a household, the inventory item is not found,
     * the inventory item doesn't belong to the user's household, or the product is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventoryItem(@PathVariable Integer id, @Valid @RequestBody InventoryItemUpdateDto updateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            InventoryItemDto inventoryItem = inventoryService.updateInventoryItem(email, id, updateDto);
            return ResponseEntity.ok(inventoryItem);
        } catch (IllegalStateException e) {
            log.info("Update inventory item failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes an inventory item from the authenticated user's household.
     *
     * @param id the ID of the inventory item
     * @return ResponseEntity with status 200 OK if successful, or an error message if the
     * user is not found, doesn't have a household, the inventory item is not found,
     * or the inventory item doesn't belong to the user's household
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventoryItem(@PathVariable Integer id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            inventoryService.deleteInventoryItem(email, id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            log.info("Delete inventory item failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Gets the preparedness status of the authenticated user's household.
     *
     * @return ResponseEntity with the preparedness status if successful, or an error message if the
     * user is not found or doesn't have a household
     */
    @GetMapping("/status")
    public ResponseEntity<?> getInventoryStatus() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            InventoryStatusDto status = inventoryService.getHouseholdInventoryStatus(email);
            return ResponseEntity.ok(status);
        } catch (IllegalStateException e) {
            log.info("Get inventory status failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
