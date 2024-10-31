package school.faang.user_service.service.recommendation;


import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final ServiceRecommendationValidator serviceRecommendationValidator;

    public Long giveRecommendation(RecommendationDto recommendation) {
        log.info("Start of recommendation {} processing", recommendation);
        serviceRecommendationValidator.checkingThePeriodOfFasting(recommendation.getAuthorId(),recommendation.getReceiverId());
        serviceRecommendationValidator.checkingTheSkillsOfRecommendation(recommendation.getSkillOffers());

        return null;
    }
}
