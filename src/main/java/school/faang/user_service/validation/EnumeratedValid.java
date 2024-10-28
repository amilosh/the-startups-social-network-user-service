package school.faang.user_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GoalStatusValidator.class)
public @interface EnumeratedValid {
    Class<? extends Enum<?>> enumClass();
    String message() default "Invalid value for status.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
