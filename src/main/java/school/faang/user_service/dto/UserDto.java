package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.User;
@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
