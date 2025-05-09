package stud.ntnu.backend.dto.group;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for summarizing a pending group invitation.
 * <p>
 * This DTO contains only the essential information needed to display a pending invitation:
 * <ul>
 *   <li>The invitation's unique identifier</li>
 *   <li>A summary of the group that sent the invitation</li>
 * </ul>
 * </p>
 * <p>
 * Used primarily in the group invitation management endpoints to return pending invitations
 * to users.
 * </p>
 */
@Data
public class GroupInvitationSummaryDto {
    /**
     * The unique identifier of the invitation.
     */
    private Integer id;

    /**
     * A summary of the group that sent the invitation.
     * Contains basic group information like name and ID.
     */
    private GroupSummaryDto group;
}