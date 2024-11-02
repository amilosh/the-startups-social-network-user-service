package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.InvalidMentorshipRejectException;
import school.faang.user_service.exception.InvalidMentorshipRequestException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestValidator {
    private static final long ID_LOWER_BORDER = 1;
    private final MentorshipRequestRepository repository;
    private final UserValidator userValidator;

    public void validateMentorshipRequest(MentorshipRequestDto dto) {
        validateSelfRequest(dto);
        validateOneRequestPerThreeMonth(dto);
        validateRequesterAndReceiverExists(dto);
    }

    public void validateNullOrBlankDescription(MentorshipRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            log.warn("Mentorship description is missing or blank.");
            throw new InvalidMentorshipRequestException("Mentorship description is missing or blank.");
        }
        log.info("Mentorship request id '{}' successfully validated for null or blank.", dto.getId());
    }

    public void validateMentorshipRequestExists(long id) {
        if (!repository.existsById(id)) {
            log.warn("Mentorship request with id '{}' not exists.", id);
            throw new EntityNotFoundException("Mentorship request with id '" + id + "' not exists.");
        }
        log.info("Mentorship request id '{}' exists.", id);
    }

    public void validateNullOrUnavailableId(Long id) {
        if (id == null || id < ID_LOWER_BORDER) {
            log.warn("Request id cannot be null or negative.");
            throw new InvalidMentorshipRequestException("Id is null or negative.");
        }
        log.info("Mentorship request id '{}' successfully validated.", id);
    }

    public void validateRequesterHasReceiverAsMentor(User requester, User receiver) {
        if (requester.getMentors().contains(receiver)) {
            log.warn("Requester id '{}' already has mentor id '{}'.", requester.getId(), receiver.getId());
            throw new InvalidMentorshipRequestException("Requester id '" + requester.getId() +
                    "' already has mentor id '" + receiver.getId() + "'.");
        }
        log.info("User '{}' has no User '{}' as mentor.", requester.getId(), receiver.getId());
    }

    public void validateNullOrBlankRejectReason(RejectionDto dto) {
        if (dto.getReason() == null || dto.getReason().isBlank()) {
            log.warn("Rejection reason is missing or blank.");
            throw new InvalidMentorshipRejectException("Rejection reason is missing or blank.");
        }
        log.info("Rejection reason successfully validated for null or blank.");
    }

    private void validateOneRequestPerThreeMonth(MentorshipRequestDto dto) {
        repository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId())
                .filter(request -> !request.getCreatedAt().isBefore(getThreeMonthAgo()))
                .ifPresent(request -> {
                    log.warn("Cannot request mentorship more then 1 time per 3 months.");
                    throw new InvalidMentorshipRequestException("Cannot request mentorship more then" +
                            " 1 time per 3 months.");
                });
        log.info("User '{}' did not request mentorship from User '{}' in the last 3 months.",
                dto.getRequesterId(), dto.getReceiverId());
    }

    private void validateRequesterAndReceiverExists(MentorshipRequestDto dto) {
        userValidator.isUserExists(dto.getRequesterId());
        userValidator.isUserExists(dto.getReceiverId());
        log.info("All users in request id '{}' exist.", dto.getId());
    }

    private void validateSelfRequest(MentorshipRequestDto dto) {
        if (dto.getRequesterId().equals(dto.getReceiverId())) {
            log.warn("Self-mentorship requests are not allowed.");
            throw new InvalidMentorshipRequestException("Self-mentorship requests are not allowed.");
        }
        log.info("User '{}' is not self-requesting mentorship.", dto.getRequesterId());
    }

    private LocalDateTime getThreeMonthAgo() {
        return LocalDateTime.now(ZoneId.of("UTC")).minusMonths(3);
    }
}
