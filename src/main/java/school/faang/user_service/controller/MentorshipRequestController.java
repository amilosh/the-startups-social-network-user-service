package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestController {
    private final MentorshipRequestService requestService;
    private final MentorshipRequestValidator requestValidator;

    public void requestMentorship(MentorshipRequestDto dto) {
        requestValidator.validateNullOrBlankDescription(dto);
        requestService.requestMentorship(dto);
    }
}
