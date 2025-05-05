package stud.ntnu.backend.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InventoryChangeEvent {

  private final Integer householdId;
  private final String changeType; // "DELETE", "UPDATE", etc.
} 