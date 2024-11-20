package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.premium.Premium;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @Size(min = 1, max = 255, message = "The name pattern should be between 1 and 255 characters long")
    private String namePattern;

    @Size(min = 1, max = 255, message = "The phone pattern should be between 1 and 255 characters long")
    private String phonePattern;

    @Size(min = 1, max = 255, message = "The email pattern should be between 1 and 255 characters long")
    private String emailPattern;

    @Size(min = 1, max = 255, message = "The about pattern should be between 1 and 255 characters long")
    private String aboutPattern;

    @Size(min = 1, max = 255, message = "The contact pattern should be between 1 and 255 characters long")
    private String contactPattern;

    @Size(min = 1, max = 255, message = "The country pattern should be between 1 and 255 characters long")
    private String countryPattern;

    @Size(min = 1, max = 255, message = "The city pattern should be between 1 and 255 characters long")
    private String cityPattern;

    @Size(min = 1, max = 255, message = "The skill pattern should be between 1 and 255 characters long")
    private String skillPattern;

    private int experienceMin;

    private int experienceMax;

    private int page;

    private int pageSize;

    private Long id;

    private Premium premium;
}
