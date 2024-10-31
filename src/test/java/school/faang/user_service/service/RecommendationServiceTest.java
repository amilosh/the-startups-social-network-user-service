package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    RecommendationRepository recommendationRepository;

    @Mock
    SkillOfferRepository skillOfferRepository;

    @Mock
    RecommendationValidator recommendationValidator;

    @Spy
    RecommendationMapperImpl recommendationMapper;

    @InjectMocks
    RecommendationService recommendationService;

    RecommendationDto dto;
    Recommendation recommendation;

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

        recommendation = recommendationMapper.toEntity(dto);
        recommendation.setId(10L);
    }

    @Test
    void testRecommendationNotFound() {
        when(recommendationRepository.create(
                dto.getAuthorId(),
                dto.getReceiverId(),
                dto.getContent()))
                .thenReturn(10L);
        when(recommendationRepository.findById(10L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationService.create(dto));
        assertEquals("Recommendation not found", exception.getMessage());
    }

    @Test
    void testCreate() {
        when(recommendationRepository.create(
                dto.getAuthorId(),
                dto.getReceiverId(),
                dto.getContent()))
                .thenReturn(10L);
        when(recommendationRepository.findById(10L)).thenReturn(Optional.of(recommendation));

        RecommendationDto result = recommendationService.create(dto);

        verify(recommendationValidator, times(1)).validateTimeAfterLastRecommendation(dto);
        verify(recommendationValidator, times(1)).validateSkillExists(dto);

        assertEquals(10L, result.getId());
    }
}