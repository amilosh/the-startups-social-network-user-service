package school.faang.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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

    @Schema(description = "Filter by username (case-insensitive partial match)")
    private String username;

    @Schema(description = "Filter by email (exact match)", example = "user@example.com")
    @Pattern(regexp = ".+@.+\\..+", message = "Email must be valid")
    private String email;

    @Schema(description = "Filter by country (exact match, case-insensitive)")
    private String country;

    @Schema(description = "Filter by city (case-insensitive partial match)")
    private String city;

    @Schema(description = "Filter by minimum experience in years", example = "0", minimum = "0")
    @Min(value = 0, message = "Experience cannot be less than 0")
    private Integer experienceMin;

    @Schema(description = "Filter by maximum experience in years", example = "10", minimum = "0")
    @Min(value = 0, message = "Experience cannot be less than 0")
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
