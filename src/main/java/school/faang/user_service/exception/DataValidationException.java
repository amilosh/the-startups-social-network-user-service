package school.faang.user_service.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DataValidationException extends RuntimeException {
    private String description;
}
