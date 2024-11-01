package school.faang.user_service.validation.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestDtoValidator {
    public static final int MIN_REQUEST_INTERVAL = 3;

    private final UserRepository userRepository;
    private final MentorshipRequestRepository requestRepository;

    public void validateCreationRequest(MentorshipRequestCreationDto creationRequestDto) {
        Long requesterId = creationRequestDto.getRequesterId();
        Long receiverId = creationRequestDto.getReceiverId();

        validateUserExistence(requesterId, receiverId);
        validateDifferentUsers(requesterId, receiverId);
        validateLastRequestDate(requesterId, receiverId);
    }

    private void validateUserExistence(Long... userIds) {
        Arrays.stream(userIds)
                .forEach(userId -> {
                    if (!userRepository.existsById(userId)) {
                        throw new EntityNotFoundException("User with ID %d does not exist in the database!".formatted(userId));
                    }
                });
    }

    private void validateDifferentUsers(Long firstUserId, Long secondUserId) {
        if (firstUserId.equals(secondUserId)) {
            throw new DataValidationException(
                    "The requester and receiver of a mentorship request cannot have the same ID. User ID: %d"
                            .formatted(firstUserId)
            );
        }
    }

    private void validateLastRequestDate(Long requesterId, Long receiverId) {
        Optional<MentorshipRequest> request = requestRepository.findLatestRequest(requesterId, receiverId);
        if (request.isPresent()) {
            LocalDateTime dateThreshold = LocalDateTime.now().minusMonths(MIN_REQUEST_INTERVAL);
            if (request.get().getCreatedAt().isBefore(dateThreshold)) {
                throw new DataValidationException(
                        "The last mentorship request from user %d to user %d was sent less than %d months ago!"
                                .formatted(requesterId, receiverId, MIN_REQUEST_INTERVAL));
            }
        }
    }

    private void validateIdIsNotNull(Long id) {
        if (id == null) {
            throw new DataValidationException("ID must not be null!");
        }
    }

    public MentorshipRequest validateAcceptRequest(Long requestId) {
        MentorshipRequest request = validateRequest(requestId);
        if (requestRepository.existAcceptedRequest(request.getRequester().getId(), request.getReceiver().getId())) {
            throw new DataValidationException(
                    "The mentorship request from user %d to user %d has already been accepted!".formatted(request.getRequester().getId(), request.getReceiver().getId())
            );
        }

        return request;
    }

    public MentorshipRequest validateRejectRequest(Long requestId) {
        return validateRequest(requestId);
    }

    private MentorshipRequest validateRequest(Long requestId) {
        validateIdNotNull(requestId);
        MentorshipRequest request = validateRequestExistence(requestId);
        validateRequestNotProcessed(request);
        return request;
    }

    private void validateRequestNotProcessed(MentorshipRequest request) {
        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new DataValidationException("The mentorship request with ID %d has already been accepted!".formatted(request.getId()));
        }
        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new DataValidationException("The mentorship request with ID %d has already been rejected.".formatted(request.getId()));
        }
    }

    private MentorshipRequest validateRequestExistence(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "The mentorship request with ID %d does not exist in the database!".formatted(requestId))
                );
    }
}