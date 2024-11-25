package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mappings({
            @Mapping(source = "followers", target = "followersIds", qualifiedByName = "mapFriendsToIds")
    })
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    @Named("mapFriendsToIds")
    default List<Long> mapFriendsToIds(List<User> followers) {
        return followers.stream().map(User::getId).toList();
    }
}
