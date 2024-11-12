package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import school.faang.user_service.entity.Country;

import java.time.LocalDateTime;

@Builder
public record UserDto(
        Long id,
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String phone,
        @NotBlank Country country,
        @NotBlank String city,
        @NotNull @PastOrPresent LocalDateTime createdAt,
        @NotNull @PastOrPresent LocalDateTime updatedAt
) {
}
