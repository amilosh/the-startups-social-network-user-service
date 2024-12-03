package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "contactPreference.preference", target = "preference", qualifiedByName = "preferenceToInt")
    UserDto toDto(User user);

    default List<UserDto> entityStreamToDtoList(Stream<User> users) {
        return users.map(this::toDto).toList();
    }

    default List<Long> usersToIds(List<UserDto> users) {
        return users.stream()
                .map(UserDto::getId)
                .toList();
    }

    @Named("preferenceToInt")
    default int preferenceToInt(PreferredContact preference) {
        return preference != null ? preference.ordinal() : -1;
    }
}
