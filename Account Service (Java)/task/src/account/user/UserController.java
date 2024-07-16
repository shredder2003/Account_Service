package account.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {
    //Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;

    @PostMapping("signup")
    public UserDTO postAuthSignUp(@RequestBody @Valid User user) {
        return userService.signup(user);
    }

    @PostMapping("changepass")
    public PasswordChanged postAuthChangePass(@Valid @RequestBody NewPassword newPassword) {
        return userService.changePassword(newPassword);
    }

}
