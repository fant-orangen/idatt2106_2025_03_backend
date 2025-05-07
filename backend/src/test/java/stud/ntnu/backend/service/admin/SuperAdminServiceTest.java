package stud.ntnu.backend.service.admin;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stud.ntnu.backend.dto.user.UserInfoDto;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.RoleRepository;
import stud.ntnu.backend.service.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SuperAdminServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private SuperAdminService superAdminService;

    public SuperAdminServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @Tag("positive")
    class PositiveTests {

        /**
         * Test for retrieving all admins successfully.
         */
        @Test
        void testGetAllAdmins() {
            User admin = new User();
            admin.setId(1);
            admin.setEmail("admin@example.com");
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            admin.setRole(adminRole);

            when(userService.getAllUsers()).thenReturn(List.of(admin));

            List<UserInfoDto> admins = superAdminService.getAllAdmins();

            assertEquals(1, admins.size());
            assertEquals("admin@example.com", admins.get(0).getEmail());
            verify(userService, times(1)).getAllUsers();
        }

        /**
         * Test for successfully revoking admin access.
         */
        @Test
        void testRevokeAdminAccess() {
            // Create a user with the "ADMIN" role
            User user = new User();
            user.setId(1);
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            user.setRole(adminRole);

            // Create the "USER" role to be assigned after revocation
            Role userRole = new Role();
            userRole.setName("USER");

            // Mock the behavior of the userService and roleRepository
            when(userService.getUserById(1)).thenReturn(Optional.of(user));
            when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

            // Call the method to revoke admin access
            superAdminService.revokeAdminAccess(1);

            // Assert that the user's role is now "USER"
            assertEquals("USER", user.getRole().getName());
            verify(userService, times(1)).saveUser(user);
        }

        /**
         * Test for successfully granting admin access.
         */
        @Test
        void testAddAdminAccess() {
            // Create a user with the "USER" role
            User user = new User();
            user.setId(1);
            Role userRole = new Role();
            userRole.setName("USER");
            user.setRole(userRole);

            // Create the "ADMIN" role to be assigned
            Role adminRole = new Role();
            adminRole.setName("ADMIN");

            // Mock the behavior of the userService and roleRepository
            when(userService.getUserById(1)).thenReturn(Optional.of(user));
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));

            // Call the method to grant admin access
            superAdminService.addAdminAccess(1);

            // Assert that the user's role is now "ADMIN"
            assertEquals("ADMIN", user.getRole().getName());
            verify(userService, times(1)).saveUser(user);
        }

        @Nested
        @Tag("negative")
        class NegativeTests {

            /**
             * Test for failing to retrieve a user when revoking admin access.
             */
            @Test
            void testRevokeAdminAccessUserNotFound() {
                when(userService.getUserById(1)).thenReturn(Optional.empty());

                RuntimeException exception = assertThrows(RuntimeException.class, () -> superAdminService.revokeAdminAccess(1));

                assertEquals("User not found", exception.getMessage());
                verify(userService, never()).saveUser(any());
            }

            /**
             * Test for failing to find the "USER" role when revoking admin access.
             */
            @Test
            void testRevokeAdminAccessRoleNotFound() {
                User user = new User();
                user.setId(1);

                when(userService.getUserById(1)).thenReturn(Optional.of(user));
                when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

                RuntimeException exception = assertThrows(RuntimeException.class, () -> superAdminService.revokeAdminAccess(1));

                assertEquals("Role 'USER' not found", exception.getMessage());
                verify(userService, never()).saveUser(any());
            }

            /**
             * Test for failing to retrieve a user when granting admin access.
             */
            @Test
            void testAddAdminAccessUserNotFound() {
                when(userService.getUserById(1)).thenReturn(Optional.empty());

                RuntimeException exception = assertThrows(RuntimeException.class, () -> superAdminService.addAdminAccess(1));

                assertEquals("User not found", exception.getMessage());
                verify(userService, never()).saveUser(any());
            }

            /**
             * Test for failing to find the "ADMIN" role when granting admin access.
             */
            @Test
            void testAddAdminAccessRoleNotFound() {
                User user = new User();
                user.setId(1);

                when(userService.getUserById(1)).thenReturn(Optional.of(user));
                when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

                RuntimeException exception = assertThrows(RuntimeException.class, () -> superAdminService.addAdminAccess(1));

                assertEquals("Role 'ADMIN' not found", exception.getMessage());
                verify(userService, never()).saveUser(any());
            }
        }
    }}