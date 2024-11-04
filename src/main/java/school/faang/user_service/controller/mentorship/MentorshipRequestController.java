package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exception.mentorship_request.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank()) {
            throw new DataValidationException("A request for mentorship must contain a reason");
        }
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }

//    private void validation(MentorshipRequestDto recommendationDto) {
//        if (recommendationDto.getDescription() == null || recommendationDto.getDescription().isBlank()) {
//            throw new DataValidationException("A request for mentorship must contain a reason");
//        }
//    }

}
