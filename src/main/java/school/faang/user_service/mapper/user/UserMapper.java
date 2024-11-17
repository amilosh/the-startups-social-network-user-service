package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "userProfilePic.fileId", target = "userProfilePicFileId")
    @Mapping(source = "premium.id", target = "premiumId")
    UserDto toDto(User user);

    @Mapping(source = "userProfilePicFileId", target = "userProfilePic.fileId")
    @Mapping(source = "premiumId", target = "premium.id")
    User toEntity(UserDto userDto);
}
