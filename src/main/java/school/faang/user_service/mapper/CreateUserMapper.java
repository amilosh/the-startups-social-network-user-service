package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface CreateUserMapper {
    @Mapping(target = "country", ignore = true)
    User toEntity (CreateUserDto createUserDto);
}
