package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.household.EmptyHouseholdMember;
import stud.ntnu.backend.model.household.Household;

import java.util.List;

public interface EmptyHouseholdMemberRepository extends JpaRepository<EmptyHouseholdMember, Integer> {
    List<EmptyHouseholdMember> findByHousehold(Household household);
} 