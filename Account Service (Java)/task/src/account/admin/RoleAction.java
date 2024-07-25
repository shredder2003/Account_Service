package account.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RoleAction {
    @NotBlank
    String user;
    String role;
    String operation;
}
