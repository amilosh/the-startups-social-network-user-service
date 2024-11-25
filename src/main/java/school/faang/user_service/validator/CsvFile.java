package school.faang.user_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CsvFileValidator.class)
public @interface CsvFile {
    String message() default "Invalid file type. Please upload the CSV file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
