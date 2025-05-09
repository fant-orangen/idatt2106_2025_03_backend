package stud.ntnu.backend.controller.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.backend.dto.user.UserInfoDto;
import stud.ntnu.backend.model.user.Role;
import stud.ntnu.backend.model.user.User;
// Ensure AdminChecker is imported if you were to mock its static methods directly,
// but here we test its effect via UserService.
// import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.admin.SuperAdminService;
import stud.ntnu.backend.service.user.UserService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SuperAdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SuperAdminService superAdminService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SuperAdminController superAdminController;

    private Principal superAdminPrincipal;
    private Principal regularUserPrincipal;
    private Principal nonExistentUserPrincipal; // For testing IllegalStateException from AdminChecker

    private User superAdminUserAccount; // The user account for the superAdminPrincipal
    private User regularUserAccount;    // The user account for the regularUserPrincipal
    private User adminUserToManage;     // A user with ADMIN role to be managed
    private User regularUserToManage;   // A user with USER role to be managed

    private final String BASE_URL = "/api/super-admin";
    private final String ONLY_SUPER_ADMIN_MESSAGE = "Only super-administrators can access this resource";
    private final String USER_NOT_FOUND_MESSAGE = "User not found"; // From AdminChecker's IllegalStateException

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(superAdminController).build();

        // Setup Principals
        superAdminPrincipal = new UsernamePasswordAuthenticationToken("superadmin@example.com", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPERADMIN"))); // Authority string might not directly matter here as we mock UserService
        regularUserPrincipal = new UsernamePasswordAuthenticationToken("user@example.com", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        nonExistentUserPrincipal = new UsernamePasswordAuthenticationToken("unknown@example.com", null, Collections.emptyList());


        // Setup User accounts that UserService will return for the principals
        Role superAdminRole = new Role();
        superAdminRole.setName("SUPERADMIN"); // Critical: Matches AdminChecker.isSuperAdminRole
        superAdminUserAccount = new User();
        superAdminUserAccount.setEmail("superadmin@example.com");
        superAdminUserAccount.setRole(superAdminRole);
        superAdminUserAccount.setId(1);

        Role userRoleForPrincipal = new Role();
        userRoleForPrincipal.setName("USER");
        regularUserAccount = new User();
        regularUserAccount.setEmail("user@example.com");
        regularUserAccount.setRole(userRoleForPrincipal);
        regularUserAccount.setId(2);


        // Setup User objects to be managed by the controller
        Role adminRoleToManage = new Role();
        adminRoleToManage.setName("ADMIN"); // For revoking/adding admin
        adminUserToManage = new User();
        adminUserToManage.setId(10);
        adminUserToManage.setEmail("admin@example.com");
        adminUserToManage.setRole(adminRoleToManage);

        Role userRoleToManage = new Role();
        userRoleToManage.setName("USER"); // For revoking/adding admin
        regularUserToManage = new User();
        regularUserToManage.setId(11);
        regularUserToManage.setEmail("regular@example.com");
        regularUserToManage.setRole(userRoleToManage);
    }

    // Helper to mock how AdminChecker (via UserService) perceives the principal
    private void mockPrincipalAsSuperAdmin(Principal principal, boolean isSuperAdmin) {
        if (principal == null || principal.getName() == null) return;

        if (isSuperAdmin) {
            when(userService.getUserByEmail(principal.getName())).thenReturn(Optional.of(superAdminUserAccount));
        } else {
            // For a non-superadmin, return a user with a different role (e.g., regularUserAccount)
            // or any user whose role is not "SUPERADMIN".
            when(userService.getUserByEmail(principal.getName())).thenReturn(Optional.of(regularUserAccount));
        }
    }

    private void mockPrincipalAsNonExistentUser(Principal principal) {
        if (principal == null || principal.getName() == null) return;
        when(userService.getUserByEmail(principal.getName())).thenReturn(Optional.empty());
    }


    // --- Tests for getAdmins ---


    @Test
    void getAdmins_whenUserIsNotSuperAdmin_shouldReturnForbidden() throws Exception {
        mockPrincipalAsSuperAdmin(regularUserPrincipal, false);

        mockMvc.perform(get(BASE_URL + "/all")
                        .principal(regularUserPrincipal))
                .andExpect(status().isForbidden())
                .andExpect(content().string(ONLY_SUPER_ADMIN_MESSAGE));

        verify(superAdminService, never()).getAllAdmins();
    }

    @Test
    void getAdmins_whenPrincipalUserNotFoundInUserService_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsNonExistentUser(nonExistentUserPrincipal); // This principal's user won't be found by UserService

        mockMvc.perform(get(BASE_URL + "/all")
                        .principal(nonExistentUserPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_NOT_FOUND_MESSAGE)); // From AdminChecker's IllegalStateException
    }


    @Test
    void getAdmins_whenServiceThrowsException_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsSuperAdmin(superAdminPrincipal, true);
        when(superAdminService.getAllAdmins()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get(BASE_URL + "/all")
                        .principal(superAdminPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Service error"));
    }



    @Test
    void getIdByEmail_whenUserIsSuperAdminAndEmailNotFound_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsSuperAdmin(superAdminPrincipal, true);
        when(userService.getUserByEmail("unknown.user@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/user-info/unknown.user@example.com")
                        .principal(superAdminPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_NOT_FOUND_MESSAGE)); // Here, User not found is from controller logic
    }

    @Test
    void getIdByEmail_whenUserIsNotSuperAdmin_shouldReturnForbidden() throws Exception {
        mockPrincipalAsSuperAdmin(regularUserPrincipal, false); // Mock current user as non-superadmin

        mockMvc.perform(get(BASE_URL + "/user-info/admin@example.com")
                        .principal(regularUserPrincipal))
                .andExpect(status().isForbidden())
                .andExpect(content().string(ONLY_SUPER_ADMIN_MESSAGE));

        // Verify userService.getUserByEmail for the principal (AdminChecker) was called
        verify(userService).getUserByEmail(regularUserPrincipal.getName());
        // Verify userService.getUserByEmail for the path variable was NOT called
        verify(userService, never()).getUserByEmail("admin@example.com");
    }

    @Test
    void getIdByEmail_whenPrincipalUserNotFoundInUserService_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsNonExistentUser(nonExistentUserPrincipal);

        mockMvc.perform(get(BASE_URL + "/user-info/admin@example.com")
                        .principal(nonExistentUserPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_NOT_FOUND_MESSAGE)); // From AdminChecker's IllegalStateException
    }


    // --- Tests for revokeAdminAccess ---
    @Test
    void revokeAdminAccess_whenUserIsSuperAdminAndTargetIsAdmin_shouldReturnOk() throws Exception {
        mockPrincipalAsSuperAdmin(superAdminPrincipal, true);
        when(userService.getUserById(adminUserToManage.getId())).thenReturn(Optional.of(adminUserToManage));
        doNothing().when(superAdminService).revokeAdminAccess(adminUserToManage.getId());

        mockMvc.perform(put(BASE_URL + "/revoke/" + adminUserToManage.getId())
                        .principal(superAdminPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin access revoked successfully, user now has user role"));

        verify(superAdminService).revokeAdminAccess(adminUserToManage.getId());
    }

    @Test
    void revokeAdminAccess_whenUserIsSuperAdminAndTargetNotFound_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsSuperAdmin(superAdminPrincipal, true);
        when(userService.getUserById(999)).thenReturn(Optional.empty());

        mockMvc.perform(put(BASE_URL + "/revoke/999")
                        .principal(superAdminPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_NOT_FOUND_MESSAGE)); // From controller logic

        verify(superAdminService, never()).revokeAdminAccess(anyInt());
    }

    @Test
    void revokeAdminAccess_whenUserIsSuperAdminAndTargetIsNotAdmin_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsSuperAdmin(superAdminPrincipal, true);
        when(userService.getUserById(regularUserToManage.getId())).thenReturn(Optional.of(regularUserToManage)); // regularUserToManage has "USER" role

        mockMvc.perform(put(BASE_URL + "/revoke/" + regularUserToManage.getId())
                        .principal(superAdminPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The user does not have an admin role to revoke"));

        verify(superAdminService, never()).revokeAdminAccess(anyInt());
    }

    @Test
    void revokeAdminAccess_whenUserIsNotSuperAdmin_shouldReturnForbidden() throws Exception {
        mockPrincipalAsSuperAdmin(regularUserPrincipal, false);

        mockMvc.perform(put(BASE_URL + "/revoke/" + adminUserToManage.getId())
                        .principal(regularUserPrincipal))
                .andExpect(status().isForbidden())
                .andExpect(content().string(ONLY_SUPER_ADMIN_MESSAGE));

        verify(superAdminService, never()).revokeAdminAccess(anyInt());
    }

    @Test
    void revokeAdminAccess_whenPrincipalUserNotFoundInUserService_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsNonExistentUser(nonExistentUserPrincipal);

        mockMvc.perform(put(BASE_URL + "/revoke/" + adminUserToManage.getId())
                        .principal(nonExistentUserPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_NOT_FOUND_MESSAGE)); // From AdminChecker
    }


    // --- Tests for addAdminAccess ---
    @Test
    void addAdminAccess_whenUserIsSuperAdminAndTargetIsUser_shouldReturnOk() throws Exception {
        mockPrincipalAsSuperAdmin(superAdminPrincipal, true);
        when(userService.getUserById(regularUserToManage.getId())).thenReturn(Optional.of(regularUserToManage));
        doNothing().when(superAdminService).addAdminAccess(regularUserToManage.getId());

        mockMvc.perform(put(BASE_URL + "/add/" + regularUserToManage.getId())
                        .principal(superAdminPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin access granted successfully, user now has admin role"));

        verify(superAdminService).addAdminAccess(regularUserToManage.getId());
    }

    @Test
    void addAdminAccess_whenUserIsSuperAdminAndTargetNotFound_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsSuperAdmin(superAdminPrincipal, true);
        when(userService.getUserById(999)).thenReturn(Optional.empty());

        mockMvc.perform(put(BASE_URL + "/add/999")
                        .principal(superAdminPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_NOT_FOUND_MESSAGE)); // From controller logic

        verify(superAdminService, never()).addAdminAccess(anyInt());
    }


    @Test
    void addAdminAccess_whenUserIsSuperAdminAndTargetIsNotUserButNotAdmin_shouldReturnBadRequest() throws Exception {
        // Simulate target user having a role other than "USER" or "ADMIN" (e.g. "SUPERADMIN" itself, or some other custom role)
        User otherRoleUser = new User();
        otherRoleUser.setId(40);
        Role someOtherRole = new Role();
        someOtherRole.setName("ANOTHER_ROLE"); // Neither USER nor ADMIN
        otherRoleUser.setRole(someOtherRole);

        mockPrincipalAsSuperAdmin(superAdminPrincipal, true);
        when(userService.getUserById(otherRoleUser.getId())).thenReturn(Optional.of(otherRoleUser));

        mockMvc.perform(put(BASE_URL + "/add/" + otherRoleUser.getId())
                        .principal(superAdminPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The user does not have a user role to promote to admin"));

        verify(superAdminService, never()).addAdminAccess(anyInt());
    }


    @Test
    void addAdminAccess_whenUserIsNotSuperAdmin_shouldReturnForbidden() throws Exception {
        mockPrincipalAsSuperAdmin(regularUserPrincipal, false);

        mockMvc.perform(put(BASE_URL + "/add/" + regularUserToManage.getId())
                        .principal(regularUserPrincipal))
                .andExpect(status().isForbidden())
                .andExpect(content().string(ONLY_SUPER_ADMIN_MESSAGE));

        verify(superAdminService, never()).addAdminAccess(anyInt());
    }

    @Test
    void addAdminAccess_whenPrincipalUserNotFoundInUserService_shouldReturnBadRequest() throws Exception {
        mockPrincipalAsNonExistentUser(nonExistentUserPrincipal);

        mockMvc.perform(put(BASE_URL + "/add/" + regularUserToManage.getId())
                        .principal(nonExistentUserPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_NOT_FOUND_MESSAGE)); // From AdminChecker
    }
}