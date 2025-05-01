package stud.ntnu.backend.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.model.group.GroupMembershipId;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, GroupMembershipId> {
    Optional<GroupMembership> findFirstByHousehold_Id(Integer householdId);

    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    Optional<GroupMembership> findCurrentByHouseholdId(@Param("householdId") Integer householdId, @Param("now") LocalDateTime now);
} 