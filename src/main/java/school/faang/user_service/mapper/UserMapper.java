package school.faang.user_service.mapper;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;


public class UserMapper {

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return User.builder()
                .id(userDto.id())
                .username(userDto.username())
                .email(userDto.email())
                .build();
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}