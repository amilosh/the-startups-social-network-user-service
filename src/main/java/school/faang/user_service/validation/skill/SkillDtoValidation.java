package school.faang.user_service.validation.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.skill.SkillDtoFieldConstraintValidationException;
import school.faang.user_service.exception.skill.SkillDtoNullObjectValidationException;

@Component
@RequiredArgsConstructor
public class SkillDtoValidation {
    public static final int MAX_TITLE_LENGTH = 64;

    public void validate(SkillDto skillDto) {
        if(skillDto == null || skillDto.getTitle() == null) {
            throw new SkillDtoNullObjectValidationException("Объект skillDto не может быть пустым!");
        }

        if(skillDto.getTitle().isBlank()) {
            throw new SkillDtoFieldConstraintValidationException("Наименование навыка не может быть пустым!");
        }

        if(skillDto.getTitle().length() > MAX_TITLE_LENGTH) {
            throw new SkillDtoFieldConstraintValidationException("Наименование навыка превышает допустимое кол-во символов!");
        }
    }
}
