package account.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

    @Constraint(validatedBy = PasswordValidator.class)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidPassword {
        String message() default "";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
