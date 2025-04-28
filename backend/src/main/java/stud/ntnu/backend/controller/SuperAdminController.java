package stud.ntnu.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.backend.dto.user.UserInfoDto;
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
}
