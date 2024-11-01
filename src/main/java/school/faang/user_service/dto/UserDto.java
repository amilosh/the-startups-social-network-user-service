package school.faang.user_service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record UserDto(
    Long id,
    @NotNull @NotEmpty String username,
    @NotNull @NotEmpty String email
) {
}
