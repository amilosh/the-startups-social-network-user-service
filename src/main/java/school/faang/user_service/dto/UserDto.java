package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(@NotNull(message = "Id cannot be empty.") Long id,
                      @NotBlank(message = "Name cannot be empty") String username,
                      @NotBlank(message = "Email is required")
                      @Email(message = "Email address has invalid format: ${validatedValue}",
                              regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$") String email) {
}
