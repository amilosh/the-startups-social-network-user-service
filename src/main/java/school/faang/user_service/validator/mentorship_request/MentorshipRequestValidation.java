package school.faang.user_service.validator.mentorship_request;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestValidation {

    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public User validateId(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserId " + userId + " is not found"));
    }

    public void validateSameId(long receiverId, long requesterId) {
        if (receiverId == requesterId) {
            throw new IllegalArgumentException("Id " + requesterId + " is the same as receiver " + receiverId);
        }
    }

    public void validate3MonthsFromTheLastRequest(User requester, User receiver) {
        long requesterId = requester.getId();
        long receiverId = receiver.getId();
        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId);
        if(lastRequest.isEmpty()) {
            log.info("Requester with id {} has no last request by receiver with id {}", requesterId, receiverId);
            return;
        }
        LocalDateTime currentDate = LocalDateTime.now();
        long monthsBetween = ChronoUnit.MONTHS.between(lastRequest.get().getCreatedAt(), currentDate);
        if (monthsBetween < 3) {
            throw new IllegalArgumentException("For the user with the " + requester.getId() +
                    ", 3 months have not passed since the last attempt to request a mentorship");
        }
    }

    public MentorshipRequest validateRequestId(long requesterId) {
        try {
            return mentorshipRequestRepository.getReferenceById(requesterId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id " + requesterId + " is not found");
        }
    }

    public void validateOfBeingInMentorship(MentorshipRequest mentorshipRequest) {
        User receiver = mentorshipRequest.getReceiver();
        User requester = mentorshipRequest.getRequester();
        if(requester == null) {
            throw new EntityNotFoundException("Requester is not found");
        }
        if(receiver == null) {
            throw new EntityNotFoundException("Receiver is not found");
        }
        long requesterId = requester.getId();
        long receiverId = receiver.getId();
        if (receiver.getMentees().contains(requester)) {
            throw new IllegalArgumentException("Id " + requesterId + " is already on the mentis list " + receiverId);
        }
    }

    public void validateStatus(MentorshipRequest mentorshipRequest) {
        if(mentorshipRequest.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalArgumentException("Status " + mentorshipRequest.getStatus() + " is rejected");
        }
        if(mentorshipRequest.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Status " + mentorshipRequest.getStatus() + "already is accepted");
        }
    }
}
