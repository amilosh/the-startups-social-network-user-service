package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserService userService;

    /**
     * Check if a skill with the given id exists in the database.
     *
     * @param skillId the id of the skill to check
     * @return true if the skill exists, false otherwise
     */
    public boolean checkIfSkillExistsById(Long skillId) {
        return skillRepository.existsById(skillId);
    }

    /**
     * Gets a skill by id.
     *
     * @param skillId the id of the skill
     * @return the skill with the given id
     * @throws javax.persistence.EntityNotFoundException if no skill with the given id exists
     */
    public Skill getSkillById(Long skillId) {
        return skillRepository.getReferenceById(skillId);
    }
}
