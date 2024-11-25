package school.faang.user_service.dto.mentorshiprequest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO для запроса на менторство")
public class MentorshipRequestDto {
    @Schema(description = "id запроса")
    private Long id;

    @NotNull()
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @Schema(description = "id отправителя", example = "1")
    private Long requesterId;

    @NotNull()
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @Schema(description = "id получателя", example = "2")
    private Long receiverId;

    @NotNull()
    @NotBlank()
    @Size(max = 4096)
    @Schema(description = "описание запроса")
    private String description;

    @Schema(description = "статус запроса")
    private RequestStatus status;

    @Schema(description = "причина отказа")
    private String rejectionReason;

    @Schema(description = "дата создания")
    private LocalDateTime createdAt;

    @Schema(description = "дата последнего обновления")
    private LocalDateTime updatedAt;
}
