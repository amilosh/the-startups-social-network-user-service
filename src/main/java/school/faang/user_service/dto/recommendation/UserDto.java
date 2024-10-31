package school.faang.user_service.dto.recommendation;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private List<Long> skillsId;
}
