package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserFilterDto {
    private final String namePattern;
    private final String phonePattern;
    private final String emailPattern;

    private String aboutPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String skillPattern;
    private int experienceMin;
    private int experienceMax;
    private int page;
    private int pageSize;
}
