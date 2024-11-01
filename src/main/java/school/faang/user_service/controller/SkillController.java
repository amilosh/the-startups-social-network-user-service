package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);

        return skillService.create(skillDto);
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle() == null) {
            throw new DataValidationException("Скилл должен иметь название");
        }

        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Имя скилла не может быть пустым");
        }
    }
}
