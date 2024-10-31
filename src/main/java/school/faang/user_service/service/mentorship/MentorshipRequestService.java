package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestDto creationRequestDto);

    List<MentorshipRequestDto> getRequests(RequestFilterDto filter);

    MentorshipRequestDto acceptRequest(Long id);

    MentorshipRequestDto rejectRequest(long id, RejectionDto rejection);
}
