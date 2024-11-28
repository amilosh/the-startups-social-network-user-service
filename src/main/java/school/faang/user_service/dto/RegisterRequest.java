package school.faang.user_service.dto;

import jakarta.validation.Valid;

public record RegisterRequest(
        @Valid
        UserDto userDto,
        @Valid
        EventDto eventDto
) {
}
