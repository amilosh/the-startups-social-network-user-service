package school.faang.user_service.service.user_skill_guarantee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public Long createGuarantee(long userId, long skillId) {
            Long maxGuarantorId = userSkillGuaranteeRepository.findMaxGuarantorId();
            long newGuarantorId = (maxGuarantorId != null) ? maxGuarantorId + 1 : 1;

       return userSkillGuaranteeRepository.create(userId, skillId, newGuarantorId);
    }

    public boolean existsByUserIdAndSkillId(Long userId, Long skillId) {
        return userSkillGuaranteeRepository.existsByUserIdAndSkillId(userId, skillId);
    }
}
