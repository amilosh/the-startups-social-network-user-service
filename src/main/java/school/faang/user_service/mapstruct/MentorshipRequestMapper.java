package school.faang.user_service.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {
    @Mapping(target = "requester", source = "mentorshipRequestDto.requesterId")
    @Mapping(target = "receiver", source = "mentorshipRequestDto.receiverId")
    MentorshipRequestDto mapToDto(MentorshipRequest mentorshipRequest);
    MentorshipRequest mapToEntity(MentorshipRequestDto mentorshipRequestDto);
}
