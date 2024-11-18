package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class UpdateUsersRankDto {

    @NotNull
    private Map<Long, Double> usersRankByIds;

    @NotNull
    private Double maximumGrowthRating;

    @NotNull
    private Integer HALF_USER_RANK;

    @NotNull
    private Double maximumUserRating;

    @NotNull
    private Double minimumUserRating;

    @NotNull
    private Double ratingGrowthIntensive;
}
