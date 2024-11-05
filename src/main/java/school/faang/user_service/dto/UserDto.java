package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        @NotBlank String userName,
        List<Long> idsSettingGoals,
        List<Long> idsGoals,
        List<Long> idsSkills,
        boolean active
) {
}
