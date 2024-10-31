package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.RequestFilter;
import school.faang.user_service.validation.mentorship.MentorshipRequestDtoValidator;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestDtoValidator requestValidator;
    private final MentorshipRequestMapper requestMapper;
    private final List<RequestFilter> requestFilters;

    @Transactional
    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto creationRequestDto) {
        Long requesterId = creationRequestDto.getRequesterId();
        Long receiverId = creationRequestDto.getReceiverId();

        log.info(
                "Received a mentorship request! Sender ID - {}, Receiver ID - {}.",
                requesterId,
                receiverId
        );
        requestValidator.validateCreationRequest(creationRequestDto);

        MentorshipRequest request = requestMapper.toEntity(creationRequestDto);
        MentorshipRequest savedRequest = mentorshipRequestRepository.save(request);
        log.info(
                "The mentorship request has been saved in data base! Requester ID - {}, receiver ID - {}, date of creation - {}",
                requesterId, receiverId, creationRequestDto.getCreatedAt()
        );

        return requestMapper.toDto(savedRequest);
    }

    @Transactional
    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filterDto) {
        log.info("A request has been received to retrieve mentorship requests with the provided filter."); // Изменено на "provided filter" для большей ясности.

        List<MentorshipRequestDto> filteredRequestsDtos =
                requestMapper.toDtoList(
                        requestFilters.stream()
                                .filter(requestFilter -> requestFilter.isApplicable(filterDto))
                                .reduce(
                                        mentorshipRequestRepository.findAll().stream(),
                                        (requestsStream, requestFilter) -> requestFilter.apply(requestsStream, filterDto),
                                        (s1, s2) -> s1
                                )
                                .toList()
                );

        log.info("Requests matching the given filters have been successfully found! Total requests: {}", filteredRequestsDtos.size()); // Изменено на "matching" и "found" для большей ясности.
        return filteredRequestsDtos;
    }

    @Transactional
    @Override
    public MentorshipRequestDto acceptRequest(Long requestId) {
        MentorshipRequest request = requestValidator.validateAcceptRequest(requestId);

        User requester = request.getRequester();
        User receiver = request.getReceiver();
        requester.getMentors().add(receiver);
        receiver.getMentees().add(requester);

        userRepository.save(requester);
        userRepository.save(receiver);

        request.setStatus(RequestStatus.ACCEPTED);
        return requestMapper.toDto(mentorshipRequestRepository.save(request));
    }

    @Transactional
    @Override
    public MentorshipRequestDto rejectRequest(Long requestId, RejectionDto rejection) {
        MentorshipRequest request = requestValidator.validateRejectRequest(requestId);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        return requestMapper.toDto(mentorshipRequestRepository.save(request));
    }
}