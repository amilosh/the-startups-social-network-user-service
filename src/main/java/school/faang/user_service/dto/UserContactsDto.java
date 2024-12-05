package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserContactsDto {
    private final Long id;
    private final String username;
    private final String email;
    private final String phone;
    private final PreferredContact preference;

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}