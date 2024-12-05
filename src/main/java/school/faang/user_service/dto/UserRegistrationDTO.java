package school.faang.user_service.dto;

public record UserRegistrationDTO(
        String username,
        String email,
        String phone,
        String password,
        boolean active,
        String aboutMe,
        Long countryId,
        String city,
        Integer experience
) {}

