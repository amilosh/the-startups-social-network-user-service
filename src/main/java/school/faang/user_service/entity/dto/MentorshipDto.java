package school.faang.user_service.entity.dto;

public record MentorshipDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe
){}
