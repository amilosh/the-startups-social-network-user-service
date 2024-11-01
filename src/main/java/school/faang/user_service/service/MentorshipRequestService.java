package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipRequestFilter.RequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {
    private final MentorshipRequestRepository requestRepository;
    private final MentorshipRequestValidator requestValidator;
    private final MentorshipRequestMapper requestMapper;
    private final List<RequestFilter> requestFilters;

    public void requestMentorship(MentorshipRequestDto dto) {
        requestValidator.validateMentorshipRequest(dto);
        log.info("Request dto validation successful. Creating request id '{}'.", dto.getId());
        requestRepository.create(dto.getRequesterId(), dto.getReceiverId(), dto.getDescription());
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        Stream<MentorshipRequest> requests = requestRepository.findAll().stream();

        return requestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(requests, (requestsStream, filter) -> filter.apply(requestsStream, filters), (s1, s2) -> s1)
                .map(requestMapper::toDto)
                .toList();
    }
}

