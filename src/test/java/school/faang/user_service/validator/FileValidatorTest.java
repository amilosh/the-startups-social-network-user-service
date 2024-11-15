package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Files;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.FilesRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileValidatorTest {
    @Mock
    private FilesRepository filesRepository;

    @InjectMocks
    private FileValidator fileValidator;

    private Long userId = 1L;

    @Test
    void validateFileExistence_ShouldThrowException_WhenFileExists() {
        when(filesRepository.findByUserId(userId)).thenReturn(new Files());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            fileValidator.validateFileExistence(userId);
        });

        assertEquals("File already exists", exception.getMessage());
    }

    @Test
    void validateFileExistence_ShouldNotThrowException_WhenFileDoesNotExist() {
        when(filesRepository.findByUserId(userId)).thenReturn(null);

        assertDoesNotThrow(() -> fileValidator.validateFileExistence(userId));
    }
}