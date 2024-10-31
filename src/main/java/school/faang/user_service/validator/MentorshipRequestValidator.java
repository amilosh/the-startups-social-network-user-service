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

    public boolean validateMentorshipRequest(MentorshipRequestDto dto) {
        return validateSelfRequest(dto) && checkOneRequestPerThreeMonth(dto) && checkRequesterAndReceiverExists(dto);
    }

    private boolean checkOneRequestPerThreeMonth(MentorshipRequestDto dto) {
        repository.findLatestRequest(getRequesterId(dto), getReceiverId(dto))
                .filter(request -> !request.getCreatedAt().isBefore(getThreeMonthAgo()))
                .ifPresent(request -> {
                    log.warn("Cannot request mentorship more then 1 time per 3 months.");
                    throw new InvalidMentorshipRequestException("Cannot request mentorship more then" +
                            " 1 time per 3 months.");
                });
        return true;
    }

    private boolean checkRequesterAndReceiverExists(MentorshipRequestDto dto) {
        return userValidator.isUserExists(getRequesterId(dto)) && userValidator.isUserExists(getReceiverId(dto));
    }

    private boolean validateSelfRequest(MentorshipRequestDto dto) {
        if (getRequesterId(dto).equals(getReceiverId(dto))) {
            log.warn("Cannot request mentorship from yourself.");
            throw new InvalidMentorshipRequestException("Cannot request mentorship from yourself.");
        }
        return true;
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
