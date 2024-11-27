package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.ConfidentialUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "userProfilePic.fileId", target = "avatar")
    @Mapping(source = "userProfilePic.smallFileId", target = "avatarSmall")
    @Mapping(source = "contactPreference.preference", target = "preference")
    @Mapping(source = "followers", target = "followerIds", qualifiedByName = "mapUserToId")
    @Mapping(source = "followees", target = "followingsIds", qualifiedByName = "mapUserToId")
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(ConfidentialUserDto confidentialUserDto);

    List<UserDto> toDto(List<User> users);

    List<User> toEntity(List<UserDto> users);

    @Named("mapUserToId")
    default List<Long> mapFollowerToId(List<User> followers) {
        return followers.stream()
                .map(User::getId)
                .toList();
    }
}
