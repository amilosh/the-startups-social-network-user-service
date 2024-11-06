package school.faang.user_service.service.skill;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

public class SkillService {
    private SkillRepository skillRepository;

    public void assignSkillToUser(long skillId, long receiverId) {
        skillRepository.assignSkillToUser(skillId, receiverId);
    }

    public Optional<Skill> findUserSkill(long skillId, long receiverId) {
        return skillRepository.findUserSkill(skillId, receiverId);
    }

    public int countExisting(List<Long> ids){
        return skillRepository.countExisting(ids);
    }
}
