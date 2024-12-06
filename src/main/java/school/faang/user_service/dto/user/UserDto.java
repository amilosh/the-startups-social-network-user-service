package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Long telegramChatId;
    private String phone;
    private PreferredContact preference;

    public UserDto(Long id, String username, String email, Long telegramChatId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.telegramChatId = telegramChatId;
    }

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }

}
