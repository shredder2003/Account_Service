package account.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Duplicated entry in payment list")
public class DuplicateEntryInPaymentListException extends RuntimeException{
}