package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestFilterDto {
    private RequestStatus status;
    private List<Long> skillIds;
}
