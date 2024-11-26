package school.faang.user_service.dto.user;

import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;

@Data
public class UserSkillGuaranteeDto {
    private Long id;
    private UserDto user;
    private SkillDto skill;
    private UserDto guarantor;
}
