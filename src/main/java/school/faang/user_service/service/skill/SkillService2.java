package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService2 {
    private final SkillRepository skillRepository;

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
