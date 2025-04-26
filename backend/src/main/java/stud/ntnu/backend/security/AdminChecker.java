package stud.ntnu.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.service.UserService;

/**
 * Utility class for checking if a user has administrative privileges.
 */
public class AdminChecker {

  /**
   * Checks if the currently authenticated user has admin or super admin privileges.
   *
   * @param userService the user service to retrieve user information
   * @return true if the user is an admin or super admin, false otherwise
   * @throws IllegalStateException if the user is not found
   */
  public static boolean isCurrentUserAdmin(UserService userService) {
    // Get the current authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User currentUser = userService.getUserByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user has ADMIN or SUPERADMIN role
    String roleName = currentUser.getRole().getName();
    return "ADMIN".equals(roleName) || "SUPERADMIN".equals(roleName);
  }

  /**
   * Checks if the specified user has admin or super admin privileges.
   *
   * @param user the user to check
   * @return true if the user is an admin or super admin, false otherwise
   */
  public static boolean isUserAdmin(User user) {
    if (user == null || user.getRole() == null) {
      return false;
    }

    String roleName = user.getRole().getName();
    return "ADMIN".equals(roleName) || "SUPERADMIN".equals(roleName);
  }
}
