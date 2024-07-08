package account;

import account.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_SEQ", allocationSize = 1)
    private Integer id;
    private String username;
    @NotBlank
    private String name;
    @NotBlank
    private String lastname;
    @NotBlank
    @Email(regexp = "\\w+(@acme.com)$")
    private String email;
    @NotBlank
    @ValidPassword
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String authority;

    public User(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

}
