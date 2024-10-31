package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final SkillRepository skillRepo;

    public void validateSkillsForExistence(List<Skill> skills) {
        if (!isSkillsExist(skills)) {
            throw new DataValidationException("Not all skills exist");
        }
    }

    public boolean isSkillsExist(List<Skill> skills) {
        List<Long> ids = skills.stream()
                .map(Skill::getId)
                .toList();
        return skillRepo.countExisting(ids) == skills.size();
    }
}
