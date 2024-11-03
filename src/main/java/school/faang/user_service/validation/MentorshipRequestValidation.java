package school.faang.user_service.validation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
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

    public void validate3MonthsFromTheLastRequest(User requester) {
        List<MentorshipRequest> listOfMentorshipRequest = requester.getSentMentorshipRequests();
        MentorshipRequest lastRequest = listOfMentorshipRequest.get(listOfMentorshipRequest.size() - 1);
        LocalDateTime currentDate = LocalDateTime.now();
        long monthsBetween = ChronoUnit.MONTHS.between(lastRequest.getCreatedAt(), currentDate);
        if (monthsBetween < 3) {
            throw new IllegalArgumentException("For the user with the " + requester.getId() + ", 3 months have not passed since the last attempt to request a mentorship");
        }
    }

    public MentorshipRequest validateRequestId(long requesterId) {
        boolean exists = mentorshipRequestRepository.existsById(requesterId);
        if (!exists) {
            throw new IllegalArgumentException("Id " + requesterId + " is not found");
        }
        return mentorshipRequestRepository.getReferenceById(requesterId);
    }
}
