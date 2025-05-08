package stud.ntnu.backend.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.group.GroupMembership;
import stud.ntnu.backend.model.group.GroupMembershipId;
import stud.ntnu.backend.model.group.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, GroupMembershipId> {
    Optional<GroupMembership> findFirstByHousehold_Id(Integer householdId);

    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    Optional<GroupMembership> findCurrentByHouseholdId(@Param("householdId") Integer householdId, @Param("now") LocalDateTime now);

    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    Page<GroupMembership> findAllCurrentByHouseholdId(@Param("householdId") Integer householdId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    java.util.List<GroupMembership> findAllCurrentByGroupId(@Param("groupId") Integer groupId, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND gm.group.id = :groupId AND (gm.leftAt IS NULL OR gm.leftAt > :now)")
    Optional<GroupMembership> findCurrentByHouseholdIdAndGroupId(@Param("householdId") Integer householdId, @Param("groupId") Integer groupId, @Param("now") LocalDateTime now);

    @Query("SELECT gm FROM GroupMembership gm WHERE gm.household.id = :householdId AND (gm.leftAt IS NULL OR gm.leftAt > :now) AND gm.group.status = 'active'")
    Page<GroupMembership> findAllCurrentByHouseholdIdAndGroupStatus(
        @Param("householdId") Integer householdId, 
        @Param("now") LocalDateTime now,
        Pageable pageable);
} 