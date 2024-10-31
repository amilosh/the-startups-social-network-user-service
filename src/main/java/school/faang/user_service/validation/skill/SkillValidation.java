package school.faang.user_service.validation.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.skill.SkillDuplicateException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillValidation {
    private final SkillRepository skillRepository;

    public void validateDuplicate(Skill skill) {
        if(skillRepository.existsByTitle(skill.getTitle())) {
            throw new SkillDuplicateException("Такой навык уже существует");
        }
    }

    public boolean validateSkillExists(Long skillId) {
        return skillRepository.existsById(skillId);
    }
}
