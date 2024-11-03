package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "mentees", target = "menteesIds", qualifiedByName = "mentees")
    @Mapping(source = "mentors", target = "mentorsIds", qualifiedByName = "mentors")
    UserDto toDto(User user);

    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    User toUser(UserDto dto);

    @Named("mentees")
    default List<Long> getMenteesIds(List<User> user) {
        return user.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("mentors")
    default List<Long> getMentorsIds(List<User> user) {
        return user.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
