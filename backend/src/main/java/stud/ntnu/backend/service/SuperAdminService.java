package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.user.UserInfoDto;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.RoleRepository;

import java.util.List;

/**
 * Service for managing super admin operations. This service provides methods to retrieve and delete
 * admins.
 */
@Service
public class SuperAdminService {
    private final UserService userService;
    private final RoleRepository roleRepository;

    public SuperAdminService(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }
    /**
     * Retrieves all admins from the user service.
     *
     * @return a list of UserInfoDto containing admin information
     */

   public List<UserInfoDto> getAllAdmins() {
        List<User> admins = userService.getAllUsers();
        return admins.stream()
                .filter(user -> user.getRole().getName().equals("ADMIN"))
                .map(admin -> new UserInfoDto(admin.getEmail(), admin.getId()))
                .toList();
    }
    //method for revoiking admin access
    /**
     * Revokes admin access for a user by their ID.
     *
     * @param id the ID of the user to revoke admin access from
     */
    public void revokeAdminAccess(Integer id) {
        User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Retrieve the "USER" role from the database
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));

        // Set the user's role to "USER"
        user.setRole(userRole);
        userService.saveUser(user);
    }

    /**
     * Deletes an admin by their ID.
     *
     * @param id the ID of the admin to delete
     */
    public void deleteAdmin(Integer id) {
        userService.deleteUser(id);
    }
}
