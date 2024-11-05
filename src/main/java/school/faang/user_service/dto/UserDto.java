package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import school.faang.user_service.entity.Country;

import java.time.LocalDateTime;
import java.util.List;

public record UserDto(
        Long id,
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String phone,
        @NotBlank Country country,
        @NotBlank String city,
        List<Long> idsMentors,
        List<Long> idsSettingGoals,
        List<Long> idsGoals,
        List<Long> idsSkills,
        @NotNull @PastOrPresent LocalDateTime createdAt,
        @NotNull @PastOrPresent LocalDateTime updatedAt,
        boolean active
) {
}
