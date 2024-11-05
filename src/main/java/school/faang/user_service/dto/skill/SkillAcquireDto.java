package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillAcquireDto {
    private Long skillId;
    private Long userId;

    public SkillAcquireDto() {}
}
