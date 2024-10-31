package school.faang.user_service.service.mentorship;

import school.faang.user_service.controller.mentorship.RejectionDto;
import school.faang.user_service.controller.mentorship.RequestFilterDro;
import school.faang.user_service.dto.MentorshipRequestDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestDto creationRequestDto);

    List<MentorshipRequestDto> getRequests(RequestFilterDro filter);

    MentorshipRequestDto acceptRequest(Long id);

    MentorshipRequestDto rejectRequest(long id, RejectionDto rejection);
}
