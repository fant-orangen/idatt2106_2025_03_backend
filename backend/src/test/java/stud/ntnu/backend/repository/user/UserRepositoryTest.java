// src/test/java/stud/ntnu/backend/repository/user/UserRepositoryTest.java
package stud.ntnu.backend.repository.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.model.user.Role;


import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest

public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void whenFindByEmail_thenReturnUser() {
        Role userRole;

        Optional<Role> existingRoleOpt = entityManager.getEntityManager().createQuery(
                        "SELECT r FROM Role r WHERE r.name = :name", Role.class)
                .setParameter("name", "USER")
                .getResultStream().findFirst();

        if (existingRoleOpt.isPresent()) {
            userRole = existingRoleOpt.get();
        } else {
            userRole = new Role();
            userRole.setName("USER");

            userRole = entityManager.persistAndFlush(userRole); // Persist only if not found
        }

        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("1234567890");
        user.setEmailVerified(true);
        user.setIsUsing2FA(false);
        user.setLocationSharingEnabled(false);
        user.setPrivacyAccepted(true);
        user.setRole(userRole);
        entityManager.persistAndFlush(user);

        // when
        Optional<User> found = userRepository.findByEmail(user.getEmail());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(found.get().getRole()).isNotNull();
        assertThat(found.get().getRole().getName()).isEqualTo("USER");
    }

    @Test
    public void whenFindByEmail_withNonExistingEmail_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isNotPresent();
    }
}