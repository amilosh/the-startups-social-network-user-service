package school.faang.user_service.service.SkillService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SkillRepository;

@Data
@RequiredArgsConstructor
@Component
public class SkillService {

    private final SkillRepository skillRepository;

    public boolean existsById(long id) {
        return skillRepository.existsById(id);
    }
}
