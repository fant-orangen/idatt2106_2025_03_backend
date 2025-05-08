package stud.ntnu.backend.model.group;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Composite key class for GroupMembership entity.
 * This class represents the primary key for the many-to-many relationship between groups and households.
 * It is used as an embedded ID in the GroupMembership entity.
 *
 * @author NTNU
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class GroupMembershipId implements Serializable {

    /**
     * The ID of the group in the membership relationship.
     */
    private Integer groupId;

    /**
     * The ID of the household in the membership relationship.
     */
    private Integer householdId;
}
