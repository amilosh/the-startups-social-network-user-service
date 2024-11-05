package school.faang.user_service.validation.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.exception.skill.SkillAcquireDtoNullObjectValidationException;

@Component
@RequiredArgsConstructor
public class SkillAcquireDtoValidation {
    public void validate(SkillAcquireDto skillAcquireDto) {
        if(skillAcquireDto == null) {
            throw new SkillAcquireDtoNullObjectValidationException("Объект skillAcquireDto не может быть пустым!");
        }

        if(skillAcquireDto.getSkillId() == null) {
            throw new SkillAcquireDtoNullObjectValidationException("Объект skillId не может быть пустым!");
        }

        if(skillAcquireDto.getUserId() == null) {
            throw new SkillAcquireDtoNullObjectValidationException("Объект userId не может быть пустым!");
        }
    }
}
