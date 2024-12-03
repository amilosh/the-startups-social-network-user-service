package school.faang.user_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CsvFileValidator implements ConstraintValidator<CsvFile, MultipartFile> {
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public void initialize(CsvFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            addConstraintValidator(context,"File must not be null or empty");
            return false;
        }

        if (!"text/csv".equals(file.getContentType())) {
           addConstraintValidator (context,"Invalid file type. Only CSV fileas are allowed.");
           return false;
        }


        if (file.getSize() > MAX_FILE_SIZE) {
            addConstraintValidator(context,"File size exceeds the maximum limit of " + MAX_FILE_SIZE + " bytes.");
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename != null && (filename.contains("..") || filename.contains("/"))) {
           addConstraintValidator(context,"Filename contains invalid characters: " + filename);
           return false;
        }
        return true;
    }

    private void addConstraintValidator (ConstraintValidatorContext context, String message){
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
