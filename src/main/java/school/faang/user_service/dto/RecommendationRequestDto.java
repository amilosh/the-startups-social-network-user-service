package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;


public record RecommendationRequestDto(
        @NotNull(message = "Id cannot be empty.")
        Long id,
        @NotBlank(message = "The message must not be empty.")
        @Size(max = 500, message = "Message can contain no more than 500 characters.")
        String message,
        @NotNull(message = "Status must be set.")
        RequestStatus status,
        @Size(message = "There can be a maximum of 50 skills.")
        List<SkillRequestDto> skills,
        @NotNull(message = "requesterId cannot be empty.")
        Long requesterId,
        @NotNull(message = "receiverId cannot be empty.")
        Long receiverId,
        @PastOrPresent(message = "The creation date must be in the past or present tense.")
        LocalDateTime createdAt,
        @PastOrPresent(message = "Update date must be in the future or present tense.")
        LocalDateTime updatedAt
) {
}
