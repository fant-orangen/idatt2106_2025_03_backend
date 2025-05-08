package stud.ntnu.backend.dto.household;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for switching a user's active household.
 * This class encapsulates the data required to change a user's current household
 * to a different one within their list of households.
 */
@Setter
@Getter
public class HouseholdSwitchRequestDto {

    /**
     * The unique identifier of the household to switch to.
     * This ID must correspond to a household that the user is already a member of.
     */
    private Integer householdId;

    /**
     * Default constructor required for JSON deserialization.
     */
    public HouseholdSwitchRequestDto() {
    }

    /**
     * Constructs a new HouseholdSwitchRequestDto with the specified household ID.
     *
     * @param householdId the ID of the household to switch to
     */
    public HouseholdSwitchRequestDto(Integer householdId) {
        this.householdId = householdId;
    }
}