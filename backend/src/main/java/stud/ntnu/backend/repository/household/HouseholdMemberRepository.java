package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stud.ntnu.backend.model.household.HouseholdMember;

/**
 * Repository interface for managing HouseholdMember entities.
 * Provides methods to query and manipulate household member data.
 */
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Integer> {
    
    /**
     * Counts the number of household members in a specific household, excluding members of a certain type.
     *
     * @param householdId The ID of the household to count members for
     * @param excludeType The member type to exclude from the count
     * @return The number of household members matching the criteria
     */
    @Query("SELECT COUNT(hm) FROM HouseholdMember hm WHERE hm.household.id = :householdId AND hm.type != :excludeType")
    Integer countByHouseholdIdAndTypeNot(@Param("householdId") Integer householdId, @Param("excludeType") String excludeType);
    
    /**
     * Calculates the total kilocalorie requirement for all members in a specific household.
     *
     * @param householdId The ID of the household to calculate requirements for
     * @return The sum of kilocalorie requirements for all household members, or 0 if no members exist
     */
    @Query("SELECT COALESCE(SUM(hm.kcalRequirement), 0) FROM HouseholdMember hm WHERE hm.household.id = :householdId")
    Integer sumKcalRequirementByHouseholdId(@Param("householdId") Integer householdId);
}