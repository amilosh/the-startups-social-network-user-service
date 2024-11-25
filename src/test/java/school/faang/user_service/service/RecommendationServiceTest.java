package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecommendationServiceTest {

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillOfferService skillOfferService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private ServiceRecommendationValidator serviceRecommendationValidator;

    @InjectMocks
    RecommendationService recommendationService;

    private RecommendationDto recommendationDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        recommendationDto = RecommendationDto.builder().id(1L).authorId(2L).receiverId(3L).content("Test")
                .createdAt(LocalDate.of(2022, 3, 22).atStartOfDay()).build();

    }

    @Test
    void testGiveRecommendation_successful() {
        recommendationService.giveRecommendation(recommendationDto);

        verify(serviceRecommendationValidator).checkingThePeriodOfFasting(
                recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        verify(serviceRecommendationValidator).checkingTheSkillsOfRecommendation(recommendationDto.getSkillOffers());
        verify(serviceRecommendationValidator).checkingTheUserSkills(recommendationDto);

        verify(recommendationRepository).create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
    }

    @Test
    void testGiveRecommendation_periodValidationFails() {
        doThrow(new DataValidationException("Invalid period"))
                .when(serviceRecommendationValidator).checkingThePeriodOfFasting(anyLong(), anyLong());

        assertThrows(DataValidationException.class, () -> {
            recommendationService.giveRecommendation(recommendationDto);
        });

        verify(serviceRecommendationValidator, never()).checkingTheSkillsOfRecommendation(any());
        verify(serviceRecommendationValidator, never()).checkingTheUserSkills(any());
        verify(recommendationRepository, never()).create(anyLong(), anyLong(), any());
    }

    @Test
    void testGiveRecommendation_skillValidationFails() {
        doThrow(new DataValidationException("Invalid skills"))
                .when(serviceRecommendationValidator).checkingTheSkillsOfRecommendation(any());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            recommendationService.giveRecommendation(recommendationDto);
        });

        assertEquals("Invalid skills", dataValidationException.getMessage());

        verify(serviceRecommendationValidator).checkingThePeriodOfFasting(
                recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        verify(serviceRecommendationValidator).checkingTheSkillsOfRecommendation(recommendationDto.getSkillOffers());

        verify(serviceRecommendationValidator, never()).checkingTheUserSkills(any());
        verify(recommendationRepository, never()).create(anyLong(), anyLong(), any());
    }

    @Test
    void testGiveRecommendation_userSkillsValidationFails() {
        doThrow(new DataValidationException("Invalid user skills"))
                .when(serviceRecommendationValidator).checkingTheUserSkills(any());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            recommendationService.giveRecommendation(recommendationDto);
        });

        assertEquals("Invalid user skills", dataValidationException.getMessage());

        verify(serviceRecommendationValidator).checkingThePeriodOfFasting(
                recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        verify(serviceRecommendationValidator).checkingTheSkillsOfRecommendation(recommendationDto.getSkillOffers());
        verify(serviceRecommendationValidator).checkingTheUserSkills(recommendationDto);

        verify(recommendationRepository, never()).create(anyLong(), anyLong(), any());
    }

    @Test
    void deleteRecommendation_shouldCallValidatorAndRepository() {
        recommendationService.deleteRecommendation(recommendationDto);

        verify(serviceRecommendationValidator).preparingBeforeDelete(recommendationDto);
        verify(recommendationRepository).deleteById(recommendationDto.getId());
    }

    @Test
    void deleteRecommendation_shouldThrowExceptionIfValidatorFails() {
        doThrow(new DataValidationException("Validation failed"))
                .when(serviceRecommendationValidator).preparingBeforeDelete(recommendationDto);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            recommendationService.deleteRecommendation(recommendationDto);
        });

        assertEquals("Validation failed", dataValidationException.getMessage());

        verify(recommendationRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllUserRecommendations_shouldReturnListOfRecommendations() {
        Recommendation recommendation = new Recommendation();
        Pageable pageable = Pageable.unpaged();
        Page<Recommendation> recommendationsPage = new PageImpl<>(List.of(recommendation));

        when(recommendationRepository.findAllByReceiverId(3L, pageable)).thenReturn(recommendationsPage);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(3L);

        assertEquals(1, result.size());
        assertEquals(recommendationDto, result.get(0));

        verify(recommendationRepository).findAllByReceiverId(3L, pageable);
        verify(recommendationMapper).toDto(recommendation);
    }

    @Test
    void getAllUserRecommendations_shouldReturnEmptyListIfNoRecommendations() {
        Pageable pageable = Pageable.unpaged();

        Page<Recommendation> emptyPage = new PageImpl<>(Collections.emptyList());

        when(recommendationRepository.findAllByReceiverId(3L, pageable)).thenReturn(emptyPage);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(3L);

        assertTrue(result.isEmpty());

        verify(recommendationRepository).findAllByReceiverId(3L, pageable);
        verify(recommendationMapper, never()).toDto(any());
    }

    @Test
    void testUpdateRecommendation_Success() {
        Recommendation recommendation = new Recommendation();
        when(recommendationMapper.toEntity(recommendationDto)).thenReturn(recommendation);

        RecommendationDto result = recommendationService.updateRecommendation(recommendationDto);

        assertNotNull(result);

        verify(recommendationRepository).update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        verify(skillOfferService).deleteAllByRecommendationId(recommendation.getId());
        verify(serviceRecommendationValidator).checkingThePeriodOfFasting(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        verify(serviceRecommendationValidator).checkingTheSkillsOfRecommendation(recommendationDto.getSkillOffers());
        verify(serviceRecommendationValidator).checkingTheUserSkills(recommendationDto);
    }

    @Test
    void testGetAllGivenRecommendations_Success() {
        Recommendation recommendation1 = new Recommendation();
        Recommendation recommendation2 = new Recommendation();

        Long authorId = recommendationDto.getAuthorId();
        Pageable pageable = Pageable.unpaged();
        List<Recommendation> recommendationList = List.of(recommendation1, recommendation2);

        Page<Recommendation> recommendationsPage = new PageImpl<>(recommendationList);

        when(recommendationRepository.findAllByAuthorId(authorId, pageable)).thenReturn(recommendationsPage);

        RecommendationDto dto1 = new RecommendationDto();
        RecommendationDto dto2 = new RecommendationDto();
        when(recommendationMapper.toDto(recommendation1)).thenReturn(dto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(dto2);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));

        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
    }
}
