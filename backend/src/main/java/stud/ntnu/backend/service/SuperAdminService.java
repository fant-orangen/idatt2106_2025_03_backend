package stud.ntnu.backend.service;

import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.user.UserInfoDto;
import stud.ntnu.backend.model.user.User;

import java.util.List;

@Service
public class SuperAdminService {
    private final UserService userService;

    public SuperAdminService(UserService userService) {
        this.userService = userService;
    }

   public List<UserInfoDto> getAllAdmins() {
        List<User> admins = userService.getAllUsers();
        return admins.stream()
                .filter(user -> user.getRole().getName().equals("ADMIN"))
                .map(admin -> new UserInfoDto(admin.getEmail(), admin.getId()))
                .toList();
    }

    public void deleteAdmin(Integer id) {
        userService.deleteUser(id);
    }
}
