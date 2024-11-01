package school.faang.user_service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventDto(
        Long id,
        @NotBlank String title,
        @NotNull @FutureOrPresent LocalDateTime startDate,
        @NotNull @Future LocalDateTime endDate,
        @NotNull Long ownerId,
        String description
) {
}
