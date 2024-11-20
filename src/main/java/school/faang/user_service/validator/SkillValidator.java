package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.SkillDuplicateException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateDuplicate(Skill skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new SkillDuplicateException("Skill with title " + skill.getTitle() + " already exists");
        }
    }

    public void validateSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return;
        }
        long existingSkillsCount = skillRepository.countExisting(skillIds);
        if (existingSkillsCount != skillIds.size()) {
            throw new IllegalArgumentException("Some skills are not present in database");
        }
    }

    public boolean validateSkillExists(Long skillId) {
        return skillRepository.existsById(skillId);
    }
}
