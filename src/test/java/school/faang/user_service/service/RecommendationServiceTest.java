package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

public class RecommendationServiceTest {

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private ServiceRecommendationValidator serviceRecommendationValidator;

    @InjectMocks
    RecommendationService recommendationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void giveRecommendation(){
        RecommendationDto recommendationDto = RecommendationDto.builder().build();
    }



}
