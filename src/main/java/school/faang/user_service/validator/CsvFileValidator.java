package school.faang.user_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CsvFileValidator implements ConstraintValidator<CsvFile, MultipartFile> {
    @Override
    public void initialize(CsvFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file != null && "text/csv".equals(file.getContentType());
    }
}
