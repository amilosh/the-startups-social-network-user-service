package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.RequestFilterValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService requestService;
    private final MentorshipRequestValidator requestValidator;
    private final RequestFilterValidator filterValidator;

    public void requestMentorship(MentorshipRequestDto dto) {
        requestValidator.validateNullOrBlankDescription(dto);
        requestService.requestMentorship(dto);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        filterValidator.validateNullFilter(filters);
        return requestService.getRequests(filters);
    }

    public void acceptRequest(Long id) {
        requestValidator.validateNullOrUnavailableId(id);
        requestService.acceptRequest(id);
    }
}
