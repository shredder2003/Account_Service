package account.user;

import account.user.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NewPassword {
    @NotBlank
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    @JsonProperty("new_password")
    @ValidPassword
    private String newPassword;

}
