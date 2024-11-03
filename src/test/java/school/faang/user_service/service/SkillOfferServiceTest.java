package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillOfferServiceTest {

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private RecommendationServiceValidator recommendationServiceValidator;

    @InjectMocks
    private SkillOfferService skillOfferService;

    private RecommendationDto dto;

    @BeforeEach
    void setUp() {
        dto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("initial content")
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .recommendationId(1L)
                        .skillId(1L)
                        .build()))
                .build();
    }

    @Test
    void testDeleteAllByRecommendationIdSuccessful() {
        long recommendationId = 1L;

        skillOfferService.deleteAllByRecommendationId(recommendationId);

        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(recommendationId);
    }

    @Test
    void testCreateSuccessful() {
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);

        long result = skillOfferService.create(anyLong(), anyLong());

        assertEquals(1L, result);
        verify(skillOfferRepository, times(1)).create(anyLong(), anyLong());
    }

    @Test
    void testFindAllByUserIdSuccessful() {
        when(skillOfferRepository.findAllByUserId(anyLong())).thenReturn(List.of(new SkillOffer()));

        List<SkillOffer> result = skillOfferRepository.findAllByUserId(anyLong());

        assertEquals(1, result.size());
        verify(skillOfferRepository, times(1)).findAllByUserId(anyLong());
    }
}
