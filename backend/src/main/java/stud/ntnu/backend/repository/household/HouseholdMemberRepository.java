package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stud.ntnu.backend.model.household.HouseholdMember;

public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Integer> {
    @Query("SELECT COUNT(hm) FROM HouseholdMember hm WHERE hm.household.id = :householdId AND hm.type != :excludeType")
    Integer countByHouseholdIdAndTypeNot(@Param("householdId") Integer householdId, @Param("excludeType") String excludeType);
    
    @Query("SELECT COALESCE(SUM(hm.kcalRequirement), 0) FROM HouseholdMember hm WHERE hm.household.id = :householdId")
    Integer sumKcalRequirementByHouseholdId(@Param("householdId") Integer householdId);
} 