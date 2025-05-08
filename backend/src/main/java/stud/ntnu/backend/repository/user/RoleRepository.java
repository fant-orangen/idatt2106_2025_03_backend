package stud.ntnu.backend.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.user.Role;

/**
 * Repository interface for managing roles in the database.
 * Extends JpaRepository to provide basic CRUD operations for Role entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    /**
     * Finds a role by its name.
     *
     * @param name The name of the role to find
     * @return An Optional containing the found role, or empty if none exists
     */
    Optional<Role> findByName(String name);
}