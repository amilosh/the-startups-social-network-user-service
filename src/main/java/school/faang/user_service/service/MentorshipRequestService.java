package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
import school.faang.user_service.validator.ValidationContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final List<MentorshipRequestValidator> requestValidators;
    private final UserService userService;

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto requestFilterDto) {

        Stream<MentorshipRequest> mentorshipRequestStream = mentorshipRequestRepository.findAll().stream();

        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .forEach(filter -> filter.apply(mentorshipRequestStream, requestFilterDto));

        return mentorshipRequestStream.map(mentorshipRequestMapper::toDto).toList();
    }

    public MentorshipRequestDto createRequestMentorship(@NotNull MentorshipRequestDto mentorshipRequestDto) {
        ValidationContext context = new ValidationContext(this, userService);

        requestValidators.forEach(validator -> validator.validate(mentorshipRequestDto, context));

        mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterUserId(),
                mentorshipRequestDto.getReceiverUserId(),
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

    public MentorshipRequestDto rejectRequest(long id, @NotNull RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    public Optional<MentorshipRequest> findLatestRequest(long requesterId, long receiverId) {
        return mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);
    }

    private MentorshipRequest findMentorshipRequestById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("mentorship request not found"));
    }
}
