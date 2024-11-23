package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @Pattern(regexp = ".+", message = "Name cannot be empty")
    private String namePattern;

    @Pattern(regexp = ".+", message = "About cannot be empty")
    private String aboutPattern;

    @Pattern(regexp = "^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$", message = "Email must be valid")
    private String emailPattern;

    @Pattern(regexp = ".+", message = "Contact cannot be empty")
    private String contactPattern;

    @Pattern(regexp = ".+", message = "Country cannot be empty")
    private String countryPattern;

    @Pattern(regexp = ".+", message = "City cannot be empty")
    private String cityPattern;

    @Pattern(regexp = "^\\+?[0-9]{1,15}$", message = "Phone number must be valid")
    private String phonePattern;

    @Pattern(regexp = ".+", message = "Skill cannot be empty")
    private String skillPattern;

    @Min(value = 0, message = "Experience cannot be less than 0")
    private int experienceMin;

    @Min(value = 0, message = "Maximum experience cannot be 0")
    private int experienceMax;

    @Min(value = 0, message = "Page number cannot be 0")
    private int page;

    @Min(value = 1, message = "Page size cannot be 1")
    private int pageSize;
}
