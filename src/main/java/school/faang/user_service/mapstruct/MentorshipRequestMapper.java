package school.faang.user_service.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    MentorshipRequestDto mapToDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "requester", source = "requesterId", qualifiedByName = "mapToUser")
    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "mapToUser")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MentorshipRequest mapToEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("mapToUser")
    default User mapToUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
}