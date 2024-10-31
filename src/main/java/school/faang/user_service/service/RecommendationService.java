package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.RecommendationValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final UserRepository userRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.validateTimeAfterLastRecommendation(recommendationDto);
        recommendationValidator.validateSkillExists(recommendationDto);

        saveSkillOffers(recommendationDto);

        addGuarantee(recommendationDto);

        Recommendation updatedRecommendation = recommendationRepository.
                findById(recommendationRepository.create(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId(),
                        recommendationDto.getContent())).orElseThrow(
                        () -> new IllegalArgumentException("Recommendation not found")
                );
        return recommendationMapper.toDto(updatedRecommendation);
    }

    private void saveSkillOffers(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOfferDto ->
                skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId())
        );
    }

    private void addGuarantee(RecommendationDto dto) {
        List<SkillOffer> userSkillOffers = skillOfferRepository.findAllByUserId(dto.getReceiverId());
        List<Long> recommendedSkillsIds = dto.getSkillOffers().stream().map(SkillOfferDto::getSkillId).toList();

        List<Skill> skillsToGuarantee = userSkillOffers.stream()
                .filter(skillOffer -> recommendedSkillsIds.contains(skillOffer.getSkill().getId()))
                .filter(skillOffer -> !skillOffer.getRecommendation().getAuthor().getId().equals(dto.getAuthorId()))
                .map(SkillOffer::getSkill)
                .toList();

        skillsToGuarantee.forEach(skill -> {
            UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                    .user(userRepository.findById(dto.getReceiverId()).orElseThrow(() -> new IllegalArgumentException("Receiver not found")))
                    .skill(skill)
                    .guarantor(userRepository.findById(dto.getAuthorId()).orElseThrow(() -> new IllegalArgumentException("Author not found")))
                    .build();
            skill.getGuarantees().add(userSkillGuarantee);
        });
    }
}
