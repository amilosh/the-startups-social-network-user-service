package school.faang.user_service.entity.dto;

public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe
){}
