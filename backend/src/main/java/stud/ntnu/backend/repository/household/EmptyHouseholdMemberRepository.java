package stud.ntnu.backend.repository.household;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import stud.ntnu.backend.model.household.EmptyHouseholdMember;
import stud.ntnu.backend.model.household.Household;

/**
 * Repository interface for managing EmptyHouseholdMember entities.
 * Provides methods to interact with the database for EmptyHouseholdMember operations.
 */
public interface EmptyHouseholdMemberRepository extends JpaRepository<EmptyHouseholdMember, Integer> {
    
    /**
     * Finds all EmptyHouseholdMember entities associated with a specific household.
     *
     * @param household The household to search for members
     * @return A list of EmptyHouseholdMember entities associated with the given household
     */
    List<EmptyHouseholdMember> findByHousehold(Household household);
}