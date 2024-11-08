package school.faang.user_service.dto;

public record RequestFilterDto(String description,
                               Long requesterId,
                               Long receiverId,
                               RequestStatusDto status) {
}
