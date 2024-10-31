package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.exception.InvalidMentorshipRequestException;
import school.faang.user_service.service.MentorshipRequestService;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestController {
    private final MentorshipRequestService requestService;

    public void requestMentorship(MentorshipRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            log.warn("Mentorship request failed: description is missing or blank.");
            throw new InvalidMentorshipRequestException("Description is missing or blank.");
        }
        log.info("Validation for null or blank description successful.");
        requestService.requestMentorship(dto);
    }
}
