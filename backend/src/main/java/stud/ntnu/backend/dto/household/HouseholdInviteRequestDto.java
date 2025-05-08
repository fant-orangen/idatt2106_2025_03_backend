package stud.ntnu.backend.dto.household;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for household invitation requests.
 * This class represents the data structure used when inviting a user to join a household,
 * containing the email address of the invitee.
 */
@Getter
@Setter
public class HouseholdInviteRequestDto {

    /**
     * The email address of the user being invited to join the household.
     * This field is required to identify the invitee.
     */
    private String email;

    /**
     * Default constructor required for JSON deserialization.
     */
    public HouseholdInviteRequestDto() {
    }

    /**
     * Constructs a new HouseholdInviteRequestDto with the specified email.
     *
     * @param email the email address of the user being invited
     */
    public HouseholdInviteRequestDto(String email) {
        this.email = email;
    }
}