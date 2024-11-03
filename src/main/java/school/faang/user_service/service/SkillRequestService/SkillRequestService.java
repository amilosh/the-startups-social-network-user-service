package school.faang.user_service.service.SkillRequestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillRequestService {

    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;

    public void saveSkillRequests(RecommendationRequest recommendationRequestEntity, List<Long> skillIds) {

        List<SkillRequest> skillRequests = skillIds.stream()
                .map(skillId -> new SkillRequest(0, recommendationRequestEntity, skillRepository.findById(skillId)
                        .orElseThrow(() -> {
                            log.error("Skill with ID {} not found", skillId);
                            return new DataValidationException("Skill with ID " + skillId + " not found" + skillId);
                        })))
                .toList();
        skillRequestRepository.saveAll(skillRequests);
        log.info("Skill requests successfully saved for Recommendation Request ID: {}", recommendationRequestEntity.getId());
    }
}
