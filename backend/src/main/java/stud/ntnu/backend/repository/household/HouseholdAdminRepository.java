package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.household.HouseholdAdmin;
import stud.ntnu.backend.model.user.User;

public interface HouseholdAdminRepository extends JpaRepository<HouseholdAdmin, Integer> {

  boolean existsByUser(User user);

  HouseholdAdmin findByUser(User user);
}