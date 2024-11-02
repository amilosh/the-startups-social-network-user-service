package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class MentorshipRequestCreationDto {

    @NotBlank(message = "The description of the reasons for mentorship cannot be empty.")
    @Size(min = 1, max = 4096)
    private String description;

    @NotNull
    private Long requesterId;

    @NotNull
    private Long receiverId;
}
