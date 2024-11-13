package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;

    public boolean existsById(Long skillId) {
        return skillRepository.existsById(skillId);
    }
}
