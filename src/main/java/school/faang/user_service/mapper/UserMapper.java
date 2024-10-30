package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toDto(User user);

    default List<UserDto> toListDto(List<User> users){
        return users.stream().map(this::toDto).toList();
    }

    default List<User> toListUser(List<UserDto> users){
        return users.stream().map(this::toUser).toList();
    }

}
