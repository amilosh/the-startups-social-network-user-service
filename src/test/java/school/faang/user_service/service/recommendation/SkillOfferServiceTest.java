package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SkillOfferValidator;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillOfferServiceTest {
    private static final long RECOMMENDATION_ID = 1L;

    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationService recommendationService;
    @Mock
    private SkillOfferValidator skillOfferValidator;
    @InjectMocks
    private SkillOfferService skillOfferService;

    @Test
    void testSaveSkillOffersOffersValidated() {
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(1L).build(),
                SkillOfferDto.builder().skillId(2L).build()
        );
        when(recommendationService.recommendationExists(anyLong())).thenReturn(true);
        skillOffers.forEach(skillOffer -> when(skillRepository.findById(skillOffer.getSkillId()))
                .thenReturn(Optional.of(Skill.builder().id(skillOffer.getSkillId()).build())));

        skillOfferService.saveSkillOffers(skillOffers, RECOMMENDATION_ID);

        skillOffers.forEach(offer -> {
            verify(skillOfferValidator).validate(offer);
        });
    }

    @Test
    void testSaveSkillOffersWithNullRecommendationId() {
        List<SkillOfferDto> skillOffers = List.of();
        assertThrows(NullPointerException.class,
                () -> skillOfferService.saveSkillOffers(skillOffers, null));
    }

    @Test
    void testSaveSkillOffersWithInvalidRecommendationId() {
        List<SkillOfferDto> skillOffers = List.of();
        long id = 1L;
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillOfferService.saveSkillOffers(skillOffers, id));
        assertEquals("Recommendation with id " + id + " not found", exception.getMessage());
    }

    @Test
    void testSaveSkillOffersWithNonExistentSkill() {
        long skillId = 1L;
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(skillId).build()
        );
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());
        when(recommendationService.recommendationExists(anyLong())).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillOfferService.saveSkillOffers(skillOffers, RECOMMENDATION_ID));
        assertEquals("Skill not found", exception.getMessage());
    }

    @Test
    void testSaveSkillOffersSaved() {
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(1L).build(),
                SkillOfferDto.builder().skillId(2L).build()
        );
        skillOffers.forEach(skillOffer -> when(skillRepository.findById(skillOffer.getSkillId()))
                .thenReturn(Optional.of(Skill.builder().id(skillOffer.getSkillId()).build())));
        when(recommendationService.recommendationExists(anyLong())).thenReturn(true);
        skillOffers.forEach(skillOffer ->
                when(skillOfferRepository.create(skillOffer.getSkillId(), RECOMMENDATION_ID))
                        .thenReturn(skillOffer.getSkillId())
        );

        skillOfferService.saveSkillOffers(skillOffers, RECOMMENDATION_ID);

        skillOffers.forEach(skillOffer -> {
            verify(skillOfferRepository).create(skillOffer.getSkillId(), RECOMMENDATION_ID);
            assertEquals(skillOffer.getSkillId(), skillOffer.getId());
        });
    }
}