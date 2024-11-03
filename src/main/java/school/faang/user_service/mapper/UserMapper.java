package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    default List<UserDto> entityStreamToDtoList(Stream<User> users) {
        return users.map(this::toDto).toList();
    }
}
