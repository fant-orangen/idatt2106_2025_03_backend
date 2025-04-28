package stud.ntnu.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.user.Role;

import java.util.Optional;

/**
 * Repository interface for managing roles in the database. Extends JpaRepository to provide CRUD
 * operations.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}