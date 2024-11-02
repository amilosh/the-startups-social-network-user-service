package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
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
    private MentorshipRequest request;

    public void requestMentorship(MentorshipRequestDto dto) {
        requestValidator.validateMentorshipRequest(dto);
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

    public void acceptRequest(long id) {
        request = validateAndGetMentorshipRequest(id);
        User requester = request.getRequester();
        User receiver = request.getReceiver();

        requestValidator.validateRequesterHasReceiverAsMentor(requester, receiver);
        requester.getMentors().add(receiver);
        request.setStatus(RequestStatus.ACCEPTED);
    }

    public void rejectRequest(long id, RejectionDto rejectionDto) {
        request = validateAndGetMentorshipRequest(id);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());
    }

    private MentorshipRequest validateAndGetMentorshipRequest(long id) {
        requestValidator.validateMentorshipRequestExists(id);
        MentorshipRequest request = requestRepository.findById(id).orElseThrow(() -> {
            log.warn("Request with id '{}' is Null", id);
            return new EntityNotFoundException("Request with id '" + id + "' is Null");
        });
        return request;
    }
}

