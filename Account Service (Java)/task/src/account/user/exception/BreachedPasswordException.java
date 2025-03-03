package account.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The password is in the hacker's database!")
public class BreachedPasswordException extends RuntimeException {
}
