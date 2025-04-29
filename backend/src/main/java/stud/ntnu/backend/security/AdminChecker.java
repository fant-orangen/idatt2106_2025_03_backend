package stud.ntnu.backend.security;

import org.springframework.security.core.context.SecurityContextHolder;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.service.UserService;

import java.security.Principal;

/**
 * Utility class for checking if a user has administrative privileges.
 */
public class AdminChecker {

  /**
   * Checks if a role name represents an admin role.
   *
   * @param roleName the role name to check
   * @return true if the role is an admin role, false otherwise
   */
  private static boolean isAdminRole(String roleName) {
    return "ADMIN".equals(roleName) || "SUPERADMIN".equals(roleName);
  }
  
  /**
   * Checks if a role name represents a super admin role.
   *
   * @param roleName the role name to check
   * @return true if the role is a super admin role, false otherwise
   */
  private static boolean isSuperAdminRole(String roleName) {
    return "SUPERADMIN".equals(roleName);
  }

  /**
   * Gets a user by email.
   *
   * @param email the user's email
   * @param userService the user service to retrieve user information
   * @return the user
   * @throws IllegalStateException if the user is not found
   */
  private static User getUserByEmail(String email, UserService userService) {
    return userService.getUserByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));
  }
  
  /**
   * Gets the current user from the principal.
   *
   * @param principal the principal containing user information
   * @param userService the user service to retrieve user information
   * @return the current user
   * @throws IllegalStateException if the user is not found
   */
  private static User getCurrentUser(Principal principal, UserService userService) {
    return getUserByEmail(principal.getName(), userService);
  }

  /**
   * Checks if the user identified by the Principal has admin or super admin privileges.
   *
   * @param principal the Principal object representing the current user
   * @param userService the user service to retrieve user information
   * @return true if the user is an admin or super admin, false otherwise
   * @throws IllegalStateException if the user is not found
   */
  public static boolean isCurrentUserAdmin(Principal principal, UserService userService) {
    return isUserAdmin(getCurrentUser(principal, userService));
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
    return isAdminRole(user.getRole().getName());
  }
  
  /**
   * Checks if the currently authenticated user has super admin privileges.
   *
   * @param userService the user service to retrieve user information
   * @return true if the user is a super admin, false otherwise
   * @throws IllegalStateException if the user is not found
   */
  public static boolean isCurrentUserSuperAdmin(UserService userService) {
    Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication();
    return isCurrentUserSuperAdmin(principal, userService);
  }
  
  /**
   * Checks if the user identified by the Principal has super admin privileges.
   *
   * @param principal the Principal object representing the current user
   * @param userService the user service to retrieve user information
   * @return true if the user is a super admin, false otherwise
   * @throws IllegalStateException if the user is not found
   */
  public static boolean isCurrentUserSuperAdmin(Principal principal, UserService userService) {
    return isUserSuperAdmin(getCurrentUser(principal, userService));
  }

  /**
   * Checks if the specified user has super admin privileges.
   *
   * @param user the user to check
   * @return true if the user is a super admin, false otherwise
   */
  public static boolean isUserSuperAdmin(User user) {
    if (user == null || user.getRole() == null) {
      return false;
    }
    return isSuperAdminRole(user.getRole().getName());
  }
}
