package school.faang.user_service.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final RecommendationRepository recommendationRepository;
    private final ServiceRecommendationValidator serviceRecommendationValidator;

    public void updateRecommendation(RecommendationDto upRecommendation) {
        log.info("Start of recommendation {} processing", upRecommendation);
        serviceRecommendationValidator.checkingThePeriodOfFasting(
                upRecommendation.getAuthorId(),
                upRecommendation.getReceiverId());
        serviceRecommendationValidator.checkingTheSkillsOfRecommendation(upRecommendation.getSkillOffers());

        log.info("A recommendation {} is being updated", upRecommendation);
        recommendationRepository.update(
                upRecommendation.getAuthorId(),
                upRecommendation.getReceiverId(),
                upRecommendation.getContent());
        //TODO покрыть тестами и добавить гарант
    }

    public void deleteRecommendation(long id){
        recommendationRepository.deleteById(id);
    }
}
