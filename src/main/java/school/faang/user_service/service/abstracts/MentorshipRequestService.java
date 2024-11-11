package school.faang.user_service.service.abstracts;

import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshiprequest.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto);

    MentorshipRequestDto acceptRequest(Long requestId);

    MentorshipRequestDto rejectRequest(Long requestId, RejectionDto rejectionDto);
}
