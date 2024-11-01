package school.faang.user_service.service.controller.SkillRequestService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.SkillRequestService.SkillRequestService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillRequestServiceTest {

    @InjectMocks
    private SkillRequestService skillRequestService;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Checking the saving of skill requests")
    void saveSkillRequestsTest() {
        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .build();

        Skill skill1 = Skill.builder()
                .id(1L)
                .build();

        Skill skill2 = Skill.builder()
                .id(2L)
                .build();

        List<Long> skillIds = List.of(1L, 2L);
        List<SkillRequest> expectedSkillRequests = List.of(
                new SkillRequest(0, recommendationRequest, skill1),
                new SkillRequest(0, recommendationRequest, skill2)
        );

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill2));

        skillRequestService.saveSkillRequests(recommendationRequest, skillIds);

        verify(skillRequestRepository).saveAll(expectedSkillRequests);
        verify(skillRepository).findById(1L);
        verify(skillRepository).findById(2L);
    }
}
