package school.faang.user_service.dto.mentorshiprequest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {
    private Long id;

    @NotNull()
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    private Long requesterId;

    @NotNull()
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    private Long receiverId;

    @NotNull()
    @NotBlank()
    @Size(max = 4096)
    private String description;

    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
