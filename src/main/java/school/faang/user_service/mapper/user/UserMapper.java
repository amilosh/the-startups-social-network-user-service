package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toListDto(List<User> users);

    List<User> toListUser(List<UserDto> users);

}
