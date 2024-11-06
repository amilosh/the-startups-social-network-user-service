package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;

public record UserDTO(
        @NotNull(groups = After.class)
        Long id,
        @NotNull(groups = Before.class)
        String username,
        @NotNull(groups = Before.class)
        String email
) {
}
