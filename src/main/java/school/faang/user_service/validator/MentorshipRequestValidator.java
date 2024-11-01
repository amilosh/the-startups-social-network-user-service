package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.exception.InvalidMentorshipRequestException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestValidator {
    private final MentorshipRequestRepository repository;
    private final UserValidator userValidator;

    public void validateMentorshipRequest(MentorshipRequestDto dto) {
        validateSelfRequest(dto);
        validateOneRequestPerThreeMonth(dto);
        validateRequesterAndReceiverExists(dto);
    }

    public void validateNullOrBlankDescription(MentorshipRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            log.warn("Mentorship request failed: description is missing or blank.");
            throw new InvalidMentorshipRequestException("Description is missing or blank.");
        }
    }

    private void validateOneRequestPerThreeMonth(MentorshipRequestDto dto) {
        repository.findLatestRequest(getRequesterId(dto), getReceiverId(dto))
                .filter(request -> !request.getCreatedAt().isBefore(getThreeMonthAgo()))
                .ifPresent(request -> {
                    log.warn("Cannot request mentorship more then 1 time per 3 months.");
                    throw new InvalidMentorshipRequestException("Cannot request mentorship more then" +
                            " 1 time per 3 months.");
                });
    }

    private void validateRequesterAndReceiverExists(MentorshipRequestDto dto) {
        userValidator.isUserExists(getRequesterId(dto));
        userValidator.isUserExists(getReceiverId(dto));
    }

    private void validateSelfRequest(MentorshipRequestDto dto) {
        if (getRequesterId(dto).equals(getReceiverId(dto))) {
            log.warn("Cannot request mentorship from yourself.");
            throw new InvalidMentorshipRequestException("Cannot request mentorship from yourself.");
        }
    }

    private Long getRequesterId(MentorshipRequestDto dto) {
        return dto.getRequesterId();
    }

    private Long getReceiverId(MentorshipRequestDto dto) {
        return dto.getReceiverId();
    }

    private LocalDateTime getThreeMonthAgo() {
        return LocalDateTime.now(ZoneId.of("UTC")).minusMonths(3);
    }
}
