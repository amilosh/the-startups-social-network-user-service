package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationServiceValidator recommendationServiceValidator;
    private final UserService userService;
    private final SkillOfferService skillOfferService;
    private final SkillService skillService;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationServiceValidator.validateSkillAndTimeRequirementsForGuarantee(recommendationDto);

        Recommendation recommendation = recommendationRepository.save(createRecommendationFromDto(recommendationDto));

        skillOfferService.saveSkillOffers(recommendation);
        skillService.addGuarantee(recommendationDto);

        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationServiceValidator.validateSkillAndTimeRequirementsForGuarantee(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
        Recommendation recommendation = getRecommendationById(recommendationDto.getId());
        skillOfferService.deleteAllByRecommendationId(recommendation.getId());
        skillOfferService.saveSkillOffers(recommendation);
        skillService.addGuarantee(recommendationDto);

        return recommendationMapper.toDto(getRecommendationById(recommendationDto.getId()));
    }

    public boolean delete(long id) {
        recommendationServiceValidator.validateRecommendationExistsById(id);
        recommendationRepository.deleteById(id);
        return !recommendationRepository.existsById(id);
    }

    public Recommendation getRecommendationById(Long recommendationId) {
        return recommendationRepository.findById(recommendationId).orElseThrow(() ->
                new EntityNotFoundException("Recommendation with id #" + recommendationId + " not found"));
    }

    public Recommendation createRecommendationFromDto(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation.setAuthor(userService.findUserById(recommendationDto.getAuthorId()));
        recommendation.setReceiver(userService.findUserById(recommendationDto.getReceiverId()));
        recommendation.setSkillOffers(skillOfferService.findAllByUserId(recommendationDto.getReceiverId()));
        return recommendation;
    }

    public boolean checkIfRecommendationExistsById(Long recommendationId) {
        return recommendationRepository.existsById(recommendationId);
    }
}
