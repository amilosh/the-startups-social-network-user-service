package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "username", target = "name")
    UserDto toDto(User user);
    @Mapping(source = "name", target = "username")
    User toEntity(UserDto userDto);
}
