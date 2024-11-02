package school.faang.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

@Configuration
public class AppConfig {

    @Bean
    public RecommendationRequestController recommendationRequestController(
            RecommendationRequestService recRequestController) {
        return new RecommendationRequestController(recRequestController);
    }

    @Bean
    public RecommendationRequestService recommendationRequestService(
            RecommendationRequestRepository recRequestRepo,
            SkillRepository skillRepository,
            RecommendationRepository recommendationRepository,
            SkillRequestRepository skillRequestRepository) {
        return new RecommendationRequestService(recRequestRepo,
                recommendationRepository,
                skillRepository,
                skillRequestRepository
        );
    }
}
