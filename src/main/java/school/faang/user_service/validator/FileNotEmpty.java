package school.faang.user_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileNotEmptyValidator.class)
public @interface FileNotEmpty {
    String message() default "File is empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
