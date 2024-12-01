package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillRequestService {
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;

    public SkillRequest createSkillRequest(Skill skill, RecommendationRequest request) {
        SkillRequest skillRequest = SkillRequest.builder()
                .skill(skill)
                .request(request)
                .build();
        skillRequest.setSkill(skill);
        skillRequest.setRequest(request);
        return skillRequestRepository.save(skillRequest);
    }

    public List<Skill> getSkillsByIds(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    public Skill getSkillById(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException(skillId));
    }

    public List<SkillRequest> createSkillRequests(List<Skill> skills, RecommendationRequest request) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptyList();
        }

        return skills.stream()
                .map(skill -> createSkillRequest(skill, request))
                .collect(Collectors.toList());
    }
}