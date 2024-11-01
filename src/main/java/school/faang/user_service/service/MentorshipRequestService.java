package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;


@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {
    private final MentorshipRequestRepository requestRepository;
    private final MentorshipRequestValidator requestValidator;

    public void requestMentorship(MentorshipRequestDto dto) {
        requestValidator.validateMentorshipRequest(dto);
        log.info("Request dto validation successful. Creating request id '{}'.", dto.getId());
        requestRepository.create(dto.getRequesterId(), dto.getReceiverId(), dto.getDescription());
    }
}

