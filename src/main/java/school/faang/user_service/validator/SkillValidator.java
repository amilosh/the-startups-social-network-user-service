package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
public class SkillValidator {
    private final SkillRepository skillRepository;

    public SkillValidator(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public void validateSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return;
        }

        long existingSkillsCount = skillRepository.countExisting(skillIds);
        if (existingSkillsCount != skillIds.size()) {
            throw new IllegalArgumentException("Некоторых скиллов нет в базе данных");
        }
    }
}
