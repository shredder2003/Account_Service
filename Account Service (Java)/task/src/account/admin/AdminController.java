package account.admin;

import account.user.CurrentUser;
import account.user.UserDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@RestController()
@RequestMapping("/api/admin/user/")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PutMapping("/role")
    public UserDTO putAdminUserRole(@RequestBody(required = false) @Valid RoleAction roleAction) {
        return adminService.processRoleAction(roleAction);
    }

    @DeleteMapping(value = {"/{email}", ""})
    public Object deleteAdminUser(@PathVariable(required = false) String email) {
        return adminService.deleteUser(email==null?"":email.toLowerCase(Locale.ROOT));
    }

    @GetMapping
    public List<UserDTO> getAdminUser() {
        return adminService.listUser();
    }

}
