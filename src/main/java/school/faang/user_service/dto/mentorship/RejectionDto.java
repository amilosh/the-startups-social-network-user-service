package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RejectionDto {
    private Long id;

    private String reason;

    @NotNull(message = "The requester's ID must not be null")
    private Long requesterId;

    @NotNull(message = "The receiver's ID must not be null")
    private Long receiverId;

}
