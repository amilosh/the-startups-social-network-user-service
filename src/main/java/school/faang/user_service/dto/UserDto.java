package school.faang.user_service.dto;

import lombok.Value;

@Value
public class UserDto {
    private Long id;
    private String username;
    private String email;
}