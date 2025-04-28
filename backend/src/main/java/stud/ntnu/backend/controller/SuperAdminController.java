package stud.ntnu.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.user.UserInfoDto;
import stud.ntnu.backend.model.user.User;
import stud.ntnu.backend.repository.user.UserRepository;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.SuperAdminService;
import stud.ntnu.backend.service.UserService;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {
    private final SuperAdminService superAdminService;
    private final UserService userService;

    public SuperAdminController(SuperAdminService superAdminService, UserService userService) {
        this.superAdminService = superAdminService;
        this.userService = userService;
    }

    /**
     * Retrieves all admins.
     *
     * @param principal the authenticated user
     * @return a list of all admins or a 403 error if unauthorized
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAdmins(Principal principal) {
        try {
            // Check if the current user is an admin using AdminChecker
            if (!AdminChecker.isCurrentUserSuperAdmin(principal, userService)) {
                return ResponseEntity.status(403).body("Only super-administrators can access this resource");
            }

            // Retrieve and return the list of admins
            List<UserInfoDto> admins = superAdminService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * Revokes admin access for a user by their ID.
     *
     * @param id the ID of the user to revoke admin access from
     * @return a success message or an error message
     */
    @PutMapping("/revoke/{id}")
    public ResponseEntity<?> revokeAdminAccess(Principal principal, @PathVariable Integer id) {
        try {
            // Check if the current user is a super admin
            if (!AdminChecker.isCurrentUserSuperAdmin(principal, userService)) {
                return ResponseEntity.status(403).body("Only super-administrators can access this resource");
            }

            // Retrieve the user and check if they have the "ADMIN" role
            User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
            if (!"ADMIN".equals(user.getRole().getName())) {
                return ResponseEntity.status(400).body("The user does not have an admin role to revoke");
            }

            // Revoke admin access for the user
            superAdminService.revokeAdminAccess(id);
            return ResponseEntity.ok("Admin access revoked successfully, user now has user role");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
