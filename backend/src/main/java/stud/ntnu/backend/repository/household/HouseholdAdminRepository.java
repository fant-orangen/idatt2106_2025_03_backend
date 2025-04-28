package stud.ntnu.backend.repository.household;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.householdAdmin.HouseholdAdmin;
import stud.ntnu.backend.model.householdAdmin.HouseholdAdminId;
import stud.ntnu.backend.model.user.User;

public interface HouseholdAdminRepository extends JpaRepository<HouseholdAdmin, HouseholdAdminId> {
    boolean existsByUser(User user);
    HouseholdAdmin findByUser(User user);
} 