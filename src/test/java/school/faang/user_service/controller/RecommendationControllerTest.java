package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private RecommendationValidator recommendationValidator;

    @InjectMocks
    private RecommendationController recommendationController;

    private RecommendationDto dto;

    @BeforeEach
    void setUp() {
        dto = RecommendationDto.builder()
                .id(1L)
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
    void testCreateRecommendation() {
        when(recommendationService.create(dto)).thenReturn(dto);

        ResponseEntity<RecommendationDto> result = recommendationController.createRecommendation(dto);

        verify(recommendationService, times(1)).create(dto);

        assertNotNull(result);
        assertEquals(dto, result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testUpdateRecommendation() {
        when(recommendationService.update(dto)).thenReturn(dto);

        ResponseEntity<RecommendationDto> result = recommendationController.updateRecommendation(dto);

        assertNotNull(result);
        assertEquals(dto, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(recommendationService, times(1)).update(dto);
    }

    @Test
    void testDeleteRecommendationSuccess() {

        recommendationController.deleteRecommendation(dto.getId());

        verify(recommendationService, times(1)).delete(dto.getId());
    }

    @Test
    void testGetAllUserRecommendations() {
        dto.setId(1L);
        when(recommendationService.getAllUserRecommendations(dto.getReceiverId())).thenReturn(List.of(dto));

        ResponseEntity<List<RecommendationDto>> result = recommendationController.getAllUserRecommendations(dto.getReceiverId());

        assertNotNull(result);
        assertEquals(1, result.getBody().size());
        assertEquals(dto, result.getBody().get(0));

        verify(recommendationService, times(1)).getAllUserRecommendations(dto.getReceiverId());
    }

    @Test
    void testGetAllGivenRecommendations() {
        dto.setId(1L);
        when(recommendationService.getAllGivenRecommendations(dto.getAuthorId())).thenReturn(List.of(dto));

        ResponseEntity<List<RecommendationDto>> result = recommendationController.getAllGivenRecommendations(dto.getAuthorId());

        assertNotNull(result);
        assertEquals(1, result.getBody().size());
        assertEquals(dto, result.getBody().get(0));

        verify(recommendationService, times(1)).getAllGivenRecommendations(dto.getAuthorId());
    }
}
