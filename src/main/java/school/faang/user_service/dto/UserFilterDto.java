package school.faang.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterDto {
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

    @Schema(description = "Filter by user name")
    @NotBlank(message = "Name cannot be empty")
    private String namePattern;

    @Schema(description = "Filter by 'About Me' content")
    @Pattern(regexp = ".*", message = "About cannot be empty")
    private String aboutPattern;

    @Schema(description = "Filter by email (exact match)", example = "user@example.com")
    @Pattern(regexp = ".+@.+\\..+", message = "Email must be valid")
    private String emailPattern;

    @Schema(description = "Filter by contact information (case-insensitive partial match)")
    @Pattern(regexp = ".*", message = "Contact cannot be empty")
    private String contactPattern;

    @Schema(description = "Filter by country (case-insensitive partial match)")
    @Pattern(regexp = ".*", message = "Country cannot be empty")
    private String countryPattern;

    @Schema(description = "Filter by city (case-insensitive partial match)")
    @Pattern(regexp = ".*", message = "City cannot be empty")
    private String cityPattern;

    @Schema(description = "Filter by phone number", example = "+1234567890")
    @Pattern(regexp = "\\+?[0-9]*", message = "Phone number must be valid")
    private String phonePattern;

    @Schema(description = "Filter by skill (case-insensitive partial match)")
    @Pattern(regexp = ".*", message = "Skill cannot be empty")
    private String skillPattern;

    @Schema(description = "Minimum years of experience", example = "0", minimum = "0")
    @Min(value = 0, message = "Experience cannot be less than 0")
    private Integer experienceMin;

    @Schema(description = "Maximum years of experience", example = "10", minimum = "0")
    @Min(value = 0, message = "Maximum experience cannot be less than 0")
    private Integer experienceMax;

    @Schema(description = "Page number (zero-based)", example = "0", minimum = "0", defaultValue = "0")
    @Min(value = 0, message = "Page number cannot be less than 0")
    @Builder.Default
    private Integer page = DEFAULT_PAGE_NUMBER;

    @Schema(description = "Number of items per page", example = "10", minimum = "1", defaultValue = "10")
    @Min(value = 1, message = "Page size cannot be less than 1")
    @Builder.Default
    private Integer pageSize = DEFAULT_PAGE_SIZE;
}
