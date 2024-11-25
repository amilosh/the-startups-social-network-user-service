package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {
    private Long id;

    @NotEmpty(message = "The description should not be empty")
    @NotBlank(message = "The description should not be blank")
    @Size(min = 1, max = 4096, message = "The description must be between 1 and 4096 characters")
    private String description;

    @NotNull(message = "The requester's ID must not be null")
    private Long requesterId;

    @NotNull(message = "The receiver's ID must not be null")
    private Long receiverId;

    private LocalDateTime createdAt;
}
