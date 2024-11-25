package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUsersRankDto {

    @NotNull
    private Map<Long, Double> usersRankByIds;

    @NotNull
    private Double maximumGrowthRating;

    @NotNull
    private Double halfUserRank;

    @NotNull
    private Double maximumUserRating;

    @NotNull
    private Double minimumUserRating;

    @NotNull
    private Double ratingGrowthIntensive;
}
