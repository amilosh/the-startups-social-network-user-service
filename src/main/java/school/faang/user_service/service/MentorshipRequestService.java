package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestValidator validator;
    private final UserService userService;

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto requestFilterDto) {

        Stream<MentorshipRequest> mentorshipRequestStream = mentorshipRequestRepository.findAll().stream();

        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .forEach(filter -> filter.apply(mentorshipRequestStream, requestFilterDto));

        return mentorshipRequestStream.map(mentorshipRequestMapper::toDto).toList();
    }

    public MentorshipRequestDto createRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        long requesterUserId = mentorshipRequestDto.getRequesterUserId();
        long receiverUserId = mentorshipRequestDto.getReceiverUserId();

        validator.validate(
                mentorshipRequestDto,
                userService.existsById(requesterUserId),
                userService.existsById(receiverUserId),
                findLatestRequest(requesterUserId, receiverUserId).getCreatedAt()
        );

        mentorshipRequestRepository.create(
                requesterUserId,
                receiverUserId,
                mentorshipRequestDto.getDescription()
        );

        return mentorshipRequestDto;
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);

        User requesterUser = mentorshipRequest.getRequester();
        User receiverUser = mentorshipRequest.getReceiver();

        if (requesterUser.getMentors().contains(receiverUser)) {
            throw new IllegalStateException("Mentorship request already accepted");
        }

        requesterUser.getMentors().add(receiverUser);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    public MentorshipRequest findLatestRequest(long requesterId, long receiverId) {
        return mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .orElseThrow(() -> new EntityNotFoundException("latest mentorship request not found"));
    }

    private MentorshipRequest findMentorshipRequestById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("mentorship request not found"));
    }
}
