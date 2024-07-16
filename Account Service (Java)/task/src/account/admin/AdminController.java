package account.admin;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    @PutMapping("/api/admin/user/role")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String putAdminUserRole() {
        return "Y";
    }

    @DeleteMapping("/api/admin/user")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteAdminUser() {
        return "Y";
    }

    @GetMapping("/api/admin/user")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String getAdminUser() {
        return "Y";
    }

}
