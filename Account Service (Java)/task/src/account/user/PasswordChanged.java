package account.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordChanged {
    @NotBlank
    private String email;
    @NotBlank
    private String status = "The password has been updated successfully";

}
