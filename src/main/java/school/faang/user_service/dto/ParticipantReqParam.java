package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;

public record ParticipantReqParam(@Min(1) Long participantId) {
}
