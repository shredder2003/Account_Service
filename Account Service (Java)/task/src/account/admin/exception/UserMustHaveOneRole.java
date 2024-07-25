package account.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The user must have at least one role!")
public class UserMustHaveOneRole extends RuntimeException{
}
