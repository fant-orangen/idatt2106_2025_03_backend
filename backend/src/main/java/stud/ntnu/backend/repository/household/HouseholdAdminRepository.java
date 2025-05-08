package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.household.Household;
import stud.ntnu.backend.model.household.HouseholdAdmin;
import stud.ntnu.backend.model.user.User;

import java.util.List;
import java.util.Optional;

public interface HouseholdAdminRepository extends JpaRepository<HouseholdAdmin, Integer> {

  boolean existsByUser(User user);

  Optional<HouseholdAdmin> findByUser(User user);

  List<HouseholdAdmin> findByHousehold(Household household);
}