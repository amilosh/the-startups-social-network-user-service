package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserDto(
        Long id,
        @NotBlank  String username,
        List<Long> idsSettingGoals,
        List<Long> idsGoals,
        List<Long> idsSkills,
        boolean active
) {
}
