package account.validation;

import account.exception.BreachedPasswordException;
import account.exception.DuplicateEntryInPaymentListException;
import account.exception.PasswordLengthException;
import account.payment.Payment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class ListUniqueValidator implements ConstraintValidator<UniqueList, List<Payment>> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean isValid(List<Payment> paymentList, ConstraintValidatorContext context) {
        logger.info("ListUniqueValidator.isValid paymentList.size="+paymentList.size());

        if (paymentList == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (paymentList.stream().distinct().count() != (long) paymentList.size()) {
            throw new DuplicateEntryInPaymentListException();
        }
        return true;
    }
}
