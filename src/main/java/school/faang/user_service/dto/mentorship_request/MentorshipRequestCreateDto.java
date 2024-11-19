package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestCreateDto {

    @NotBlank(message = "Description most not be empty or blank.")
    private String description;

    @NotNull(message = "RequesterId is required.")
    @Positive(message = "RequesterId must be greater than 0.")
    private Long requesterId;

    @NotNull(message = "ReceiverId is required.")
    @Positive(message = "ReceiverId must be greater than 0.")
    private Long receiverId;
}
