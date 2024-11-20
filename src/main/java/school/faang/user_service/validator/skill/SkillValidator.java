package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final SkillRepository skillRepository;

    public void validateTitle(SkillDto skillDto) {
        if (skillDto.getTitle() == null || skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Название навыка не может быть пустым");
        }

        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Навык с таким названием уже существует");
        }
    }

    public Skill skillAlreadyExists(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Такого навыка в БД не существует"));
    }
}
