package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MentorshipRequestCreationDto {
    @NotBlank(message = "The description of the reasons for mentorship cannot be empty.")
    @Size(min = 1, max = 4096)
    private String description;

    @NotNull
    @Positive
    private Long requesterId;

    @NotNull
    @Positive
    private Long receiverId;
}
