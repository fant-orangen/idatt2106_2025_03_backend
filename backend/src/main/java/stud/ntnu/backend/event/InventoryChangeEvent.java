package stud.ntnu.backend.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event class representing a change in a household's inventory.
 * This event is used to notify listeners when inventory items are modified,
 * allowing them to take appropriate actions such as sending notifications
 * about low supplies.
 *
 * @see InventoryEventListener
 */
@Getter
@RequiredArgsConstructor
public class InventoryChangeEvent {

  /**
   * The ID of the household whose inventory was changed.
   */
  private final Integer householdId;

  /**
   * The type of change that occurred in the inventory.
   * Possible values include "DELETE", "UPDATE", etc.
   */
  private final String changeType;
}