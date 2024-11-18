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
}
