package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.FilesRepository;

@Component
@RequiredArgsConstructor
public class FileValidator {
    private final FilesRepository filesRepository;

    public void validateFileExistence(Long userId) {
        if (filesRepository.findByUserId(userId) != null) {
            throw new DataValidationException("File already exists");
        }
    }
}
