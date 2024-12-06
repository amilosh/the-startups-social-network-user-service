package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    default List<UserDto> entityStreamToDtoList(Stream<User> users) {
        return users.map(this::toDto).toList();
    }
}
