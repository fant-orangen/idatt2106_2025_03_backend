package stud.ntnu.backend.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import stud.ntnu.backend.dto.user.UserInfoDto;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.RoleRepository;
import stud.ntnu.backend.service.user.UserService;

import java.util.List;

/**
 * Service for managing super admin operations. This service provides methods to retrieve, add, and
 * revoke admin access for users in the system.
 */
@Service
@RequiredArgsConstructor
public class SuperAdminService {

  private final UserService userService;
  private final RoleRepository roleRepository;

  /**
   * Retrieves all users with admin role from the system.
   *
   * @return a list of UserInfoDto containing admin information (email and ID)
   */
  public List<UserInfoDto> getAllAdmins() {
    List<User> admins = userService.getAllUsers();
    return admins.stream()
        .filter(user -> user.getRole().getName().equals("ADMIN"))
        .map(admin -> new UserInfoDto(admin.getEmail(), admin.getId()))
        .toList();
  }

  /**
   * Revokes admin access for a user by changing their role to regular user.
   *
   * @param id the ID of the user to revoke admin access from
   * @throws RuntimeException if the user or USER role is not found
   */
  public void revokeAdminAccess(Integer id) {
    User user = userService.getUserById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Role userRole = roleRepository.findByName("USER")
        .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));

    user.setRole(userRole);
    userService.saveUser(user);
  }

  /**
   * Grants admin access to a user by changing their role to admin.
   *
   * @param id the ID of the user to grant admin access to
   * @throws RuntimeException if the user or ADMIN role is not found
   */
  public void addAdminAccess(Integer id) {
    User user = userService.getUserById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Role adminRole = roleRepository.findByName("ADMIN")
        .orElseThrow(() -> new RuntimeException("Role 'ADMIN' not found"));

    user.setRole(adminRole);
    user.setIsUsing2FA(true);
    userService.saveUser(user);
  }
}
