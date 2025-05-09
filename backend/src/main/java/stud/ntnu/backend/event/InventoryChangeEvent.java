package stud.ntnu.backend.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event class representing a change in a household's inventory. This event is used to notify
 * listeners when inventory items are modified, allowing them to take appropriate actions such as
 * sending notifications about low supplies or updating inventory statistics.
 *
 * <p>The event contains information about which household's inventory was changed
 * and what type of change occurred. This information can be used by listeners to determine what
 * actions to take in response to the change.</p>
 *
 * @see InventoryEventListener
 */
@Getter
@RequiredArgsConstructor
public class InventoryChangeEvent {

  /**
   * The ID of the household whose inventory was changed. This ID is used to identify which
   * household's inventory was affected by the change event.
   */
  private final Integer householdId;

  /**
   * The type of change that occurred in the inventory. Possible values include:
   * <ul>
   *   <li>"DELETE" - An item was removed from the inventory</li>
   *   <li>"UPDATE" - An existing item was modified</li>
   *   <li>"ADD" - A new item was added to the inventory</li>
   * </ul>
   */
  private final String changeType;
}