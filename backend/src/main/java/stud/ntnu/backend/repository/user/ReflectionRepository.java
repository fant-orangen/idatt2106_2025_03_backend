package stud.ntnu.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.user.Reflection;

/**
 * Repository interface for Reflection entity operations.
 */
@Repository
public interface ReflectionRepository extends JpaRepository<Reflection, Integer> {
    // Basic CRUD operations are provided by JpaRepository
    // Custom query methods can be added as needed
}