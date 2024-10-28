package school.faang.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @NotBlank(message = "The name cannot be empty.")
    @Pattern(message = "Bad formed person name: ${validatedValue}",
            regexp = "^[A-Z][a-z]*(\\s(([a-z]{1,3})|(([a-z]+\\')?[A-Z][a-z]*)))*$")
    private String namePattern;

    @Size(max = 500, message = "The field 'about' can contain no more than 500 characters.")
    private String aboutPattern;

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email address has invalid format: ${validatedValue}",
            regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String emailPattern;

    private String contactPattern;

    @Pattern(regexp = "^[A-Z][a-z]*(\\s(([a-z]{1,3})|(([a-z]+\\')?[A-Z][a-z]*)))*$",
            message = "Country must start with an uppercase letter.")
    private String countryPattern;

    @Pattern(regexp = "^[A-Z][a-z]*(\\s(([a-z]{1,3})|(([a-z]+\\')?[A-Z][a-z]*)))*$",
            message = "City must start with an uppercase letter.")
    private String cityPattern;

    @Pattern(message = "Bad formatted phone number: ${validatedValue}",
            regexp = "^\\+\\d{1,3} \\d{1,4} \\d{3}-\\d{4}$")
    private String phonePattern;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Skill must contain only letters and spaces.")
    private String skillPattern;

    @Min(value = 0, message = "Minimum experience cannot be less than 0.")
    private Integer experienceMin;

    @Max(value = 50, message = "Maximum experience cannot exceed 50 years.")
    private Integer experienceMax;

    @Min(value = 1, message = "Page number cannot be less than 1.")
    private int page;

    @Min(value = 1, message = "Page size must be at least 1.")
    private int pageSize;
}
