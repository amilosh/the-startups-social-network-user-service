package school.faang.user_service.dto;

public record MentorshipRequestDto(
        String description,
        long requesterId,
        long receiverId
) {
}