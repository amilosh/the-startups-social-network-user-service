package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "aboutMe", source = "user.aboutMe")
    @Mapping(target = "city", source = "user.city")
    @Mapping(target = "experience", source = "user.experience")
    UserDto toDto(User user);

    @Mapping(target = "id", source = "userDto.id")
    @Mapping(target = "username", source = "userDto.username")
    @Mapping(target = "email", source = "userDto.email")
    @Mapping(target = "phone", source = "userDto.phone")
    @Mapping(target = "aboutMe", source = "userDto.aboutMe")
    @Mapping(target = "city", source = "userDto.city")
    @Mapping(target = "experience", source = "userDto.experience")
    User toEntity(UserDto userDto);
}
