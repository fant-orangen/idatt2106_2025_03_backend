package stud.ntnu.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.backend.model.user.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}