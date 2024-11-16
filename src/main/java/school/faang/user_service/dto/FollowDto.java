package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowDto {
    @NotBlank(message = "FollowerId and FolloweeId must not be empty or blank.")
    @Positive(message = "FollowerId and FolloweeId must be greater than 0.")
    private Long followerId;

    @NotBlank(message = "FollowerId and FolloweeId must not be empty or blank.")
    @Positive(message = "FollowerId and FolloweeId must be greater than 0.")
    private Long followeeId;
}