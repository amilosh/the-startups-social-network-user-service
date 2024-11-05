package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.RequestFilterValidator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService requestService;
    private final RequestFilterValidator filterValidator;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto dto) {
        log.info("Requesting mentorship from userId #{} to userId #{}.", dto.getRequesterId(), dto.getReceiverId());
        return requestService.requestMentorship(dto);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        filterValidator.validateNullFilter(filters);
        log.info("Getting all requests by filters: {}.", filters);
        return requestService.getRequests(filters);
    }

    public MentorshipRequestDto acceptRequest(Long id) {
        log.info("Accepting request with id #{}", id);
        return requestService.acceptRequest(id);
    }

    public MentorshipRequestDto rejectRequest(Long id, RejectionDto rejectionDto) {
        log.info("Rejecting request with id #{} and reason '{}'", id, rejectionDto.getReason());
        return requestService.rejectRequest(id, rejectionDto);
    }
}