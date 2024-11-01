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
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;

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
    RecommendationServiceValidator recommendationServiceValidator;

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
                .content("initial content")
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
    void testCreate() {
        when(recommendationRepository.create(
                dto.getAuthorId(),
                dto.getReceiverId(),
                dto.getContent()))
                .thenReturn(10L);
        when(recommendationRepository.findById(10L)).thenReturn(Optional.of(recommendation));

        RecommendationDto result = recommendationService.create(dto);

        assertEquals(10L, result.getId());

        verify(recommendationServiceValidator, times(1)).validateTimeAfterLastRecommendation(dto);
        verify(recommendationServiceValidator, times(1)).validateSkillExists(dto);
    }

    @Test
    void testUpdate() {
        when(recommendationRepository.findById(10L)).thenReturn(Optional.of(recommendation));
        dto.setId(10L);

        RecommendationDto result = recommendationService.update(dto);
        result.setContent("Updated content");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Updated content", result.getContent());

        verify(recommendationServiceValidator, times(1)).validateTimeAfterLastRecommendation(dto);
        verify(recommendationServiceValidator, times(1)).validateSkillExists(dto);
        verify(recommendationRepository, times(1))
                .update(dto.getAuthorId(), dto.getReceiverId(), dto.getContent());
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(dto.getId());

    }

    @Test
    void testDelete() {
        recommendation.setId(1L);
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(false);

        boolean result = recommendationService.delete(recommendation.getId());

        assertTrue(result);
        verify(recommendationServiceValidator, times(1)).validateRecommendationExistsById(recommendation.getId());
        verify(recommendationRepository, times(1)).existsById(recommendation.getId());


    }

    @Test
    void testGetRecommendationByIdNotFound() {
        when(recommendationRepository.findById(recommendation.getId())).thenReturn(Optional.empty());

        Exception result = assertThrows(IllegalArgumentException.class, () -> recommendationService.getRecommendationById(10L));
        assertEquals("Recommendation with id #" + recommendation.getId() + " not found", result.getMessage());
    }

    @Test
    void testGetRecommendationSuccess() {
        when(recommendationRepository.findById(recommendation.getId())).thenReturn(Optional.of(recommendation));

        Recommendation result = recommendationService.getRecommendationById(recommendation.getId());

        assertEquals(recommendation.getId(), result.getId());
        verify(recommendationRepository, times(1)).findById(recommendation.getId());
    }

    @Test
    void testSaveAndReturnRecommendationNotFound() {
        when(recommendationRepository.create(
                dto.getAuthorId(),
                dto.getReceiverId(),
                dto.getContent()))
                .thenReturn(10L);
        when(recommendationRepository.findById(10L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationService.create(dto));
        assertEquals("Recommendation not found", exception.getMessage());
    }
}