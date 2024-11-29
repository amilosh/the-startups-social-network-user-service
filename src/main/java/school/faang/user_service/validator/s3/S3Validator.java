package school.faang.user_service.validator.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.DataValidationException;

import java.util.Objects;

@Slf4j
@Component
public class S3Validator {

    public void validateFileSize(MultipartFile file, Integer picMaxSize) {
        long maxSizeAsBytes = picMaxSize * 1024;
        if (file.getSize() > maxSizeAsBytes) {
            log.warn("The image could not be uploaded");
            throw new DataValidationException(String.format("The maximum file size has been exceeded. The maximum size is %d KB", picMaxSize));
        }
    }

    public void validateContentType(MultipartFile file, String contentType) {
        if (!Objects.equals(file.getContentType(), contentType)) {
            log.warn("The image could not be uploaded");
            throw new DataValidationException(String.format("Invalid content type. Need '%s'", contentType));
        }
    }
}
