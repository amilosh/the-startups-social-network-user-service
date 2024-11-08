package school.faang.user_service.service.mentorship;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship.RequestFilter;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestDtoValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorshipRequestService {

    private final MentorshipRequestRepository requestRepository;
    private final UserService userService;
    private final MentorshipRequestDtoValidator requestValidator;
    private final MentorshipRequestMapper requestMapper;
    private final List<RequestFilter> requestFilters;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestCreationDto creationRequestDto) {
        Long requesterId = creationRequestDto.getRequesterId();
        Long receiverId = creationRequestDto.getReceiverId();
        log.info(
                "Received a mentorship request! Requester ID - {}, Receiver ID - {}.",
                requesterId,
                receiverId
        );

        requestValidator.validateCreationRequest(creationRequestDto);

        MentorshipRequest request = requestMapper.toMentorshipRequest(creationRequestDto);
        request.setStatus(RequestStatus.PENDING);
        MentorshipRequest savedRequest = requestRepository.save(request);
        log.info(
                "The mentorship request has been saved in data base! Requester ID - {}, receiver ID - {}, date of creation - {}",
                requesterId, receiverId, savedRequest.getCreatedAt()
        );

        return requestMapper.toMentorshipRequestDto(savedRequest);
    }

    @Transactional
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filterDto) {
        log.info("A request has been received to retrieve mentorship requests with the provided filter.");
        Stream<MentorshipRequest> requestsToFilter = requestRepository.findAll().stream();
        List<RequestFilter> filtersToApply =
                requestFilters.stream()
                        .filter(requestFilter -> requestFilter.isApplicable(filterDto))
                        .toList();

        for (RequestFilter filter : filtersToApply) {
            requestsToFilter = filter.apply(requestsToFilter, filterDto);
        }

        List<MentorshipRequest> filteredRequests = requestsToFilter.toList();

        log.info("Requests matching the given filters have been successfully found! Total requests: {}", filteredRequests.size());
        return requestMapper.toDtoList(filteredRequests);
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(Long requestId) {
        log.info("Accepting mentorship request with ID: {}", requestId);
        MentorshipRequest request = requestValidator.validateAcceptRequest(requestId);

        User mentee = request.getRequester();
        User mentor = request.getReceiver();

        initializeLists(mentee);
        initializeLists(mentor);

        if (!mentee.getMentors().contains(mentor)) {
            mentee.getMentors().add(mentor);
            userService.saveUser(mentee);
        }
        if (!mentor.getMentees().contains(mentee)) {
            mentor.getMentees().add(mentee);
            userService.saveUser(mentor);
        }

        request.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest savedRequest = requestRepository.save(request);
        log.info("Successfully accepted request ID: {}", requestId);

        return requestMapper.toMentorshipRequestDto(savedRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(Long requestId, RejectionDto rejectionDto) {
        log.info("Rejecting mentorship request with ID: {}. Reason: {}", requestId, rejectionDto.getReason());
        MentorshipRequest request = requestValidator.validateRejectRequest(requestId);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());

        MentorshipRequest savedRequest = requestRepository.save(request);
        log.info("Successfully rejected request ID: {}", requestId);
        return requestMapper.toMentorshipRequestDto(savedRequest);
    }

    private void initializeLists(User user) {
        if (user.getMentors() == null) {
            user.setMentors(new ArrayList<>());
        }
        if (user.getMentees() == null) {
            user.setMentees(new ArrayList<>());
        }
    }
}