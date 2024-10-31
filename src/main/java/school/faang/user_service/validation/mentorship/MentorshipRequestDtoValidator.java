package school.faang.user_service.validation.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestDtoValidator {
    private static final int MIN_REQUEST_INTERVAL = 3;

    private final UserRepository userRepository;
    private final MentorshipRequestRepository requestRepository;

    public void validateCreationRequest(MentorshipRequestDto creationRequestDto) {
        Long requesterId = creationRequestDto.getRequesterId();
        Long receiverId = creationRequestDto.getReceiverId();

        validateUserExistence(requesterId);
        validateUserExistence(receiverId);
        validateDifferentUsers(requesterId, receiverId);
        validateLastRequestDate(requesterId, receiverId);
    }

    public void validateUserExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with ID %d does not exist in the database!".formatted(userId));
        }
    }

    public void validateDifferentUsers(Long firstUserId, Long secondUserId) {
        if (firstUserId.equals(secondUserId)) {
            throw new DataValidationException("User IDs cannot be the same! User IDs: %d".formatted(firstUserId));
        }
    }

    public void validateLastRequestDate(Long requesterId, Long receiverId) {
        Optional<MentorshipRequest> request = requestRepository.findLatestRequest(requesterId, receiverId);
        if (request.isPresent()) {
            LocalDateTime dateInPast = LocalDateTime.now().minusMonths(MIN_REQUEST_INTERVAL);
            if (request.get().getCreatedAt().isBefore(dateInPast)) {
                throw new DataValidationException(
                        "The last mentorship request from user %d to user %d was sent less than %d months ago!"
                                .formatted(requesterId, receiverId, MIN_REQUEST_INTERVAL));
            }
        }
    }
}
