package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.service.user_skill_guarantee.UserSkillGuaranteeService;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceRecommendationValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private SkillService skillService;

    @Mock
    private SkillOfferDto skillOfferDto;

    @Mock
    private SkillOfferService skillOfferService;

    @InjectMocks
    ServiceRecommendationValidator serviceRecommendationValidator;

    private RecommendationDto recommendationDto;
    private long authorId;
    private long receiverId;
    private Recommendation recommendation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        recommendationDto = RecommendationDto.builder().id(1L).authorId(2L).receiverId(3L).content("Test").
                createdAt(LocalDate.of(2022, 3, 22).atStartOfDay()).build();
        authorId = recommendationDto.getAuthorId();
        receiverId = recommendationDto.getReceiverId();
        recommendation = new Recommendation();
    }

    @Test
    public void testCheckingThePeriodOffFasting_NoRecommendation() {
        when(recommendationService.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() ->
                serviceRecommendationValidator.checkingThePeriodOfFasting(authorId, receiverId)
        );
    }

    @Test
    public void testCheckingThePeriodOfFasting_OrderThanSixMonthsAgo() {
        recommendation.setCreatedAt(LocalDate.now().minusMonths(7).atStartOfDay());

        when(recommendationService.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.of(recommendation));

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setCreatedAt(recommendation.getCreatedAt());
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        assertDoesNotThrow(() ->
                serviceRecommendationValidator.checkingThePeriodOfFasting(authorId, receiverId)
        );
    }

    @Test
    public void testCheckingThePeriodOfFasting_YoungerThanSixMonthsAgo() {
        recommendation.setCreatedAt(LocalDate.now().minusMonths(3).atStartOfDay());

        when(recommendationService.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.of(recommendation));

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setCreatedAt(recommendation.getCreatedAt());

        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
                serviceRecommendationValidator.checkingThePeriodOfFasting(authorId, receiverId)
        );
        assertEquals("The creation date is less than 6 months", dataValidationException.getMessage());
    }

    @Test
    public void checkingTheSkillsOfRecommendation_IsNotInSystemIsInvalid() {
        List<SkillOfferDto> skills = List.of(
                new SkillOfferDto(7L, List.of(1L, 2L)),
                new SkillOfferDto(8L, List.of(3L, 4L, 5L))
        );

        when(skillRepository.countExisting(List.of(1L, 2L))).thenReturn(2);
        when(skillRepository.countExisting(List.of(3L, 4L, 5L))).thenReturn(2);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            serviceRecommendationValidator.checkingTheSkillsOfRecommendation(skills);
        });

        assertEquals("These skills do not meet the conditions", dataValidationException.getMessage());
    }

    @Test
    public void checkingTheSkillsOfRecommendation_InSystem() {
        List<SkillOfferDto> skills = List.of(
                new SkillOfferDto(7L, List.of(1L, 2L)),
                new SkillOfferDto(8L, List.of(3L, 4L, 5L))
        );

        when(skillRepository.countExisting(List.of(1L, 2L))).thenReturn(2);
        when(skillRepository.countExisting(List.of(3L, 4L, 5L))).thenReturn(3);

        serviceRecommendationValidator.checkingTheSkillsOfRecommendation(skills);
    }

    //==============================================================================
    @Test
    public void checkingTheUserSkills_SkillIsNotPresent() {
        Long skillId = 101L;

        SkillOfferDto skillOfferDto = new SkillOfferDto(1L, List.of(skillId));
        when(recommendationDto.getSkillOffers()).thenReturn(new ArrayList<>(List.of(skillOfferDto)));
        when(skillOfferDto.getSkillsId()).thenReturn(List.of(skillId));
        when(skillService.findUserSkill(skillId, receiverId)).thenReturn(Optional.empty()); // Навык отсутствует у пользователя

        serviceRecommendationValidator.checkingTheUserSkills(recommendationDto);

        //verify(skillService).assignSkillToUser(skillId, receiverId);

       // verify(userSkillGuaranteeService, never()).createGuarantee(anyLong(), anyLong());
    }

    @Test
    public void checkingTheUserSkills_SkillIsPresent_AuthorIsNotGuarantee() {
        Long skillId = 101L;
        Long authorId = recommendationDto.getAuthorId();

        when(recommendationDto.getSkillOffers()).thenReturn(List.of(skillOfferDto));
        when(skillOfferDto.getSkillsId()).thenReturn(List.of(skillId));
        when(skillService.findUserSkill(skillId, receiverId)).thenReturn(Optional.of(mock(Skill.class)));
        when(userSkillGuaranteeService.createGuarantee(authorId, skillId)).thenReturn(null);

        serviceRecommendationValidator.checkingTheUserSkills(recommendationDto);

        verify(skillService, never()).assignSkillToUser(skillId, receiverId);

        verify(userSkillGuaranteeService).createGuarantee(authorId, skillId);
    }

    @Test
    public void checkingTheUserSkills_SkillIsPresent_AuthorIsGuarantee() {
        Long skillId = 101L;
        Long authorId = recommendationDto.getAuthorId();

        when(recommendationDto.getSkillOffers()).thenReturn(List.of(skillOfferDto));
        when(skillOfferDto.getSkillsId()).thenReturn(List.of(skillId));
        when(skillService.findUserSkill(skillId, receiverId)).thenReturn(Optional.of(mock(Skill.class))); // Навык уже есть у пользователя
        when(userSkillGuaranteeService.createGuarantee(authorId, skillId)).thenReturn(1L); // Автор является гарантом

        serviceRecommendationValidator.checkingTheUserSkills(recommendationDto);

        verify(skillService, never()).assignSkillToUser(skillId, receiverId);

        verify(userSkillGuaranteeService, never()).createGuarantee(authorId, skillId);
    }

    //==============================================================================
    @Test
    public void testPreparingBeforeDelete_DoesNotExistIsInvalid() {
        recommendationDto = new RecommendationDto();

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            serviceRecommendationValidator.preparingBeforeDelete(recommendationDto);
        });

        assertEquals("This recommendation does not exist", dataValidationException.getMessage());
    }

    @Test
    public void testPreparingBeforeDelete_Exist() {
        serviceRecommendationValidator.preparingBeforeDelete(recommendationDto);
    }
}
