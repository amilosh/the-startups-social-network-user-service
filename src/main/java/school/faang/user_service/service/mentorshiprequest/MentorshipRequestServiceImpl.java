package school.faang.user_service.service.mentorshiprequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorshiprequest.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        mentorshipRequestValidator.validateRequesterAndReceiver(requesterId, receiverId);
        mentorshipRequestValidator.validateRequestInterval(requesterId, receiverId);

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(requesterId,
                receiverId, mentorshipRequestDto.getDescription());

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        Stream<MentorshipRequest> mentorshipRequestStream = mentorshipRequestRepository.findAll().stream();

        for (var customFilter : mentorshipRequestFilters) {
            if (customFilter.isApplicable(requestFilterDto)) {
                mentorshipRequestStream = customFilter.apply(mentorshipRequestStream, requestFilterDto);
            }
        }

        return mentorshipRequestStream
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @Override
    public MentorshipRequestDto acceptRequest(Long requestId) {
        MentorshipRequest mentorshipRequest = mentorshipRequestValidator.getRequestByIdOrThrowException(requestId);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        mentorshipRequestValidator.validateRequestStatus(mentorshipRequest, RequestStatus.ACCEPTED);
        if (mentorshipRequest.getStatus().equals(RequestStatus.REJECTED)) {
            mentorshipRequest.setRejectionReason(null);
        }

        requester.getMentors().add(receiver);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Override
    public MentorshipRequestDto rejectRequest(Long requestId, RejectionDto rejectionDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestValidator.getRequestByIdOrThrowException(requestId);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        mentorshipRequestValidator.validateRequestStatus(mentorshipRequest, RequestStatus.REJECTED);
        if (mentorshipRequest.getStatus().equals(RequestStatus.ACCEPTED)) {
            requester.getMentors().remove(receiver);
        }

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectionDto.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }
}