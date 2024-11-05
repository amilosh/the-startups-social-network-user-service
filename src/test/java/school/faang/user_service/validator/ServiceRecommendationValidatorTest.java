package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.recommendation.UserSkillGuaranteeService;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceRecommendationValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @InjectMocks
    ServiceRecommendationValidator serviceRecommendationValidator;

    private RecommendationDto recommendationDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        recommendationDto = RecommendationDto.builder().id(1L).authorId(2L).receiverId(3L).content("Test").
                createdAt(LocalDate.of(2022, 3, 22).atStartOfDay()).build();

    }

    //==============================================================================
    @Test
    public void testCheckingThePeriodOffFasting_NoRecommendation() {
        //TODO

    }

    @Test
    public void testCheckingThePeriodOfFasting_OrderThanSixMonthsAgo() {
        //TODO пофиксить
    }

    @Test
    public void testCheckingThePeriodOfFasting_YoungerThanSixMonthsAgo() {
        //TODO
    }

    //==============================================================================
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

        //TODO

    }

    @Test
    public void checkingTheUserSkills_SkillIsPresent_AuthorIsNotGuarantee() {
        //TODO
    }

    @Test
    public void checkingTheUserSkills_SkillIsPresent_AuthorIsGuarantee() {
        //TODO фиксить
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, List.of(10L))));

        Skill skill = new Skill();

        when(skillRepository.findUserSkill(10L, 2L)).thenReturn(Optional.of(skill));
        when(userSkillGuaranteeService.createGuarantee(1L, 10L)).thenReturn(null); // Автор уже гарант

        // Запускаем метод
        serviceRecommendationValidator.checkingTheUserSkills(recommendationDto);

        // Проверяем, что метод createGuarantee возвращает null и вызывает логирование
        verify(log).info("The author is already the guarantor of this skill {}", skill);
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
