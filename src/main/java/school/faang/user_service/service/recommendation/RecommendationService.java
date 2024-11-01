package school.faang.user_service.service.recommendation;


import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final SubscriptionService subscriptionService;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationRepository recommendationRepository;
    private final ServiceRecommendationValidator serviceRecommendationValidator;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        log.info("Start of recommendation {} processing", recommendation);
        serviceRecommendationValidator.checkingThePeriodOfFasting(recommendation.getAuthorId(), recommendation.getReceiverId());
        serviceRecommendationValidator.checkingTheSkillsOfRecommendation(recommendation.getSkillOffers());
        //checkingTheUserSkills()

        log.info("A recommendation {} is being created", recommendation);
        recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        return recommendation;
    }

    public void deleteRecommendation(RecommendationDto delRecommendation){
        log.info("The recommendation {} is being deleted", delRecommendation);
        //TODO покрыть тестами
        subscriptionService.deleteRecommendation(delRecommendation.getId());
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> pageRecommendation = recommendationRepository.findAllByReceiverId(receiverId);
        return null;
    }
}
