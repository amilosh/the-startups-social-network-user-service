package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class UserSkillGuaranteeDto {
    private Long id;
    private UserDto user;
    private SkillDto skill;
    private UserDto guarantor;
}
