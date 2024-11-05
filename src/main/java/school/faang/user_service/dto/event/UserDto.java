package school.faang.user_service.dto.event;

import lombok.Data;

@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
}
