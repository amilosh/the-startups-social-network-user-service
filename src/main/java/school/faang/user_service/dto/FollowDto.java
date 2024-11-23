package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowDto {
    @NotNull(message = "FollowerId and FolloweeId must not be empty or blank.")
    @Positive(message = "FollowerId and FolloweeId must be greater than 0.")
    private Long followerId;

    @NotNull(message = "FollowerId and FolloweeId must not be empty or blank.")
    @Positive(message = "FollowerId and FolloweeId must be greater than 0.")
    private Long followeeId;
}