package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validation.recommendation.RecommendationControllerValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private RecommendationControllerValidator recommendationControllerValidator;

    @InjectMocks
    private RecommendationController recommendationController;

    private RecommendationDto dto;

    @BeforeEach
    void setUp() {
        dto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("text")
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .recommendationId(1L)
                        .skillId(1L)
                        .build()))
                .build();
    }

    @Test
    void testGiveRecommendation() {
        when(recommendationService.create(dto)).thenReturn(dto);

        RecommendationDto result = recommendationController.giveRecommendation(dto);

        verify(recommendationControllerValidator, times(1)).validateDto(dto);
        verify(recommendationService, times(1)).create(dto);

        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void testUpdateRecommendation() {
        when(recommendationService.update(dto)).thenReturn(dto);

        RecommendationDto result = recommendationController.updateRecommendation(dto);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(recommendationControllerValidator, times(1)).validateDto(dto);
        verify(recommendationService, times(1)).update(dto);
    }

    @Test
    void testDeleteRecommendationSuccess() {
        dto.setId(1L);
        when(recommendationService.delete(dto.getId())).thenReturn(true);

        boolean result = recommendationController.deleteRecommendation(dto.getId());

        assertTrue(result);

        verify(recommendationControllerValidator, times(1)).validateId(dto.getId());
        verify(recommendationService, times(1)).delete(dto.getId());
    }

    @Test
    void testDeleteRecommendationInvalidId() {
        dto.setId(1L);
        when(recommendationService.delete(dto.getId())).thenReturn(false);

        boolean result = recommendationController.deleteRecommendation(dto.getId());

        assertFalse(result);

        verify(recommendationControllerValidator, times(1)).validateId(dto.getId());
        verify(recommendationService, times(1)).delete(dto.getId());
    }

    @Test
    void testGetAllUserRecommendations() {
        dto.setId(1L);
        when(recommendationService.getAllUserRecommendations(dto.getReceiverId())).thenReturn(List.of(dto));

        List<RecommendationDto> result = recommendationController.getAllUserRecommendations(dto.getReceiverId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(recommendationControllerValidator, times(1)).validateId(dto.getReceiverId());
        verify(recommendationService, times(1)).getAllUserRecommendations(dto.getReceiverId());
    }

    @Test
    void testGetAllGivenRecommendations() {
        dto.setId(1L);
        when(recommendationService.getAllGivenRecommendations(dto.getAuthorId())).thenReturn(List.of(dto));

        List<RecommendationDto> result = recommendationController.getAllGivenRecommendations(dto.getAuthorId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(recommendationControllerValidator, times(1)).validateId(dto.getAuthorId());
        verify(recommendationService, times(1)).getAllGivenRecommendations(dto.getAuthorId());
    }
}
