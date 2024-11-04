package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public Skill getSkillById(long id) {
        return skillRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Skill with id: %s not found".formatted(id)));
    }
}
