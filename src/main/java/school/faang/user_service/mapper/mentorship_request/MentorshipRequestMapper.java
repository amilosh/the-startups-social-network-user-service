package school.faang.user_service.mapper.mentorship_request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MentorshipRequest toMentorshipRequest(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequest> toMentorshipRequest(List<MentorshipRequestDto> mentorshipRequestDtoList);

    List<MentorshipRequestDto> toMentorshipRequestDtoList(List<MentorshipRequest> mentorshipRequestList);

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    RejectionDto toRejectionDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "requester", ignore = true)
    MentorshipRequest toMentorshipRequest(RejectionDto rejectionDto);

}
