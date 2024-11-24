package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "requesterUserId")
    @Mapping(source = "receiver.id", target = "receiverUserId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);


    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);
}
