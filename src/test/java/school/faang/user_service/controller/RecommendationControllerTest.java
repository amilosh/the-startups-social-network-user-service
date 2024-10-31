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
import school.faang.user_service.validation.RecommendationValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

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

        verify(recommendationValidator, times(1)).validateDto(dto);
        verify(recommendationService, times(1)).create(dto);

        assertNotNull(result);
        assertEquals(dto, result);
    }
}
