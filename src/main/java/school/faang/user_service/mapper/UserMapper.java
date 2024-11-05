package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "aboutMe", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "experience", ignore = true)
    @Mapping(target = "contactPreference", ignore = true)
    User toEntity(UserDto userDto);

    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "aboutMe", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "experience", ignore = true)
    @Mapping(target = "preference", ignore = true)
    @Mapping(target = "telegramChatId", ignore = true)
    UserDto toDto(User user);
}
