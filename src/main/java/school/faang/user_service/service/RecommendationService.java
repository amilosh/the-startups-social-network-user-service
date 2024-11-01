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
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationServiceValidator recommendationServiceValidator;
    private final UserService userService;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationServiceValidator.validateTimeAfterLastRecommendation(recommendationDto);
        recommendationServiceValidator.validateSkillExists(recommendationDto);

        saveSkillOffers(recommendationDto);
        addSkillGuarantee(recommendationDto);

        Recommendation updatedRecommendation = saveAndReturnRecommendation(recommendationDto);
        return recommendationMapper.toDto(updatedRecommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationServiceValidator.validateTimeAfterLastRecommendation(recommendationDto);
        recommendationServiceValidator.validateSkillExists(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());
        saveSkillOffers(recommendationDto);
        addSkillGuarantee(recommendationDto);

        return recommendationMapper.toDto(getRecommendationById(recommendationDto.getId()));
    }

    public boolean delete(long id) {
        recommendationServiceValidator.validateRecommendationExistsById(id);
        recommendationRepository.deleteById(id);
        return !recommendationRepository.existsById(id);
    }

    public Recommendation getRecommendationById(Long recommendationId) {
        return recommendationRepository.findById(recommendationId).orElseThrow(() ->
                new IllegalArgumentException("Recommendation with id #" + recommendationId + " not found"));
    }

    private Recommendation saveAndReturnRecommendation(RecommendationDto recommendationDto) {
        return recommendationRepository.
                findById(recommendationRepository.create(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId(),
                        recommendationDto.getContent())).orElseThrow(
                        () -> new IllegalArgumentException("Recommendation not found")
                );
    }

    private void saveSkillOffers(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOfferDto ->
                skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId())
        );
    }

    private void addSkillGuarantee(RecommendationDto dto) {
        List<SkillOffer> userSkillOffers = skillOfferRepository.findAllByUserId(dto.getReceiverId());
        List<Long> recommendedSkillsIds = dto.getSkillOffers().stream().map(SkillOfferDto::getSkillId).toList();

        List<Skill> skillsToGuarantee = userSkillOffers.stream()
                .filter(skillOffer -> recommendedSkillsIds.contains(skillOffer.getSkill().getId()))
                .filter(skillOffer -> !skillOffer.getRecommendation().getAuthor().getId().equals(dto.getAuthorId()))
                .map(SkillOffer::getSkill)
                .toList();

        skillsToGuarantee.forEach(skill -> {
            UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                    .user(userService.findUserById(dto.getReceiverId()))
                    .skill(skill)
                    .guarantor(userService.findUserById(dto.getAuthorId()))
                    .build();
            skill.getGuarantees().add(userSkillGuarantee);
        });
    }
}
