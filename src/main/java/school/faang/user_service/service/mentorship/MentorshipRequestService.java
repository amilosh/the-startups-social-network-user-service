package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.request_filter.RequestFilter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<RequestFilter> requestFilters;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long receiverId = mentorshipRequestDto.getReceiverId();
        long requesterId = mentorshipRequestDto.getRequesterId();
        validateSameId(receiverId, requesterId);

        User receiver = validateId(receiverId);
        User requester = validateId(requesterId);
        validate3MonthsFromTheLastRequest(requester);

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toMentorshipRequest(mentorshipRequestDto);

        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);

        receiver.getReceivedMentorshipRequests().add(mentorshipRequest);
        requester.getSentMentorshipRequests().add(mentorshipRequest);

        mentorshipRequestRepository.save(mentorshipRequest);
        userRepository.save(requester);
        userRepository.save(receiver);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilter) {
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();
        requestFilters.stream().filter(filter -> filter.isApplicable(requestFilter)).forEach(filter -> filter.apply(mentorshipRequests, requestFilter));

        return mentorshipRequestMapper.toMentorshipRequestDtoList(mentorshipRequests.toList());
    }

    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = validateRequestId(id);
        long receiverId = mentorshipRequest.getReceiver().getId();
        long requesterId = mentorshipRequest.getRequester().getId();
        User receiver = validateId(receiverId);
        User requester = validateId(requesterId);

        if (receiver.getMentees().contains(requester)) {
            throw new IllegalArgumentException("Id " + requesterId + " is already on the mentis list " + receiverId);
        }
        receiver.getMentees().add(requester);
        requester.getMentors().add(receiver);
        userRepository.save(requester);
        userRepository.save(receiver);

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = validateRequestId(id);
        String reason = rejection.getReason();
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(reason);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    private User validateId(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserId " + userId + " is not found"));
    }

    private void validateSameId(long receiverId, long requesterId) {
        if (receiverId == requesterId) {
            throw new IllegalArgumentException("Id " + requesterId + " is the same as receiver " + receiverId);
        }
    }

    private void validate3MonthsFromTheLastRequest(User requester) {
        List<MentorshipRequest> listOfMentorshipRequest = requester.getSentMentorshipRequests();
        MentorshipRequest lastRequest = listOfMentorshipRequest.get(listOfMentorshipRequest.size() - 1);
        LocalDateTime currentDate = LocalDateTime.now();
        long monthsBetween = ChronoUnit.MONTHS.between(lastRequest.getCreatedAt(), currentDate);
        if (monthsBetween < 3) {
            throw new IllegalArgumentException("For the user with the " + requester.getId() + ", 3 months have not passed since the last attempt to request a mentorship");
        }
    }

    private MentorshipRequest validateRequestId(long requesterId) {
        boolean exists = mentorshipRequestRepository.existsById(requesterId);
        if (!exists) {
            throw new IllegalArgumentException("Id " + requesterId + " is not found");
        }
        return mentorshipRequestRepository.getReferenceById(requesterId);
    }

}
