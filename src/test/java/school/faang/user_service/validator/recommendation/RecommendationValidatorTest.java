package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {
    private static final long USER_ID = 1L;
    private static final String CONTENT = "content";
    private static final long NON_EXISTING_USER_ID = 999L;
    private static final String EXISTING_SKILL_TITLE = "Java";
    private static final String NON_EXISTING_SKILL_TITLE = "NonExistingSkill";
    private static final LocalDateTime LAST_RECOMMENDATION_DATE =
            LocalDateTime.of(2014, Month.JULY, 2, 15, 30);

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    private RequestRecommendationDto requestRecommendationDto;
    private Recommendation recommendation;

    @BeforeEach
    public void setUp() {
        requestRecommendationDto = RequestRecommendationDto.builder()
                .content(CONTENT)
                .authorId(USER_ID)
                .receiverId(USER_ID)
                .skillOffers(List.of(
                        SkillOfferDto.builder().skillTitle(EXISTING_SKILL_TITLE).build(),
                        SkillOfferDto.builder().skillTitle(NON_EXISTING_SKILL_TITLE).build()
                ))
                .build();
        recommendation = new Recommendation();
    }

    @Test
    @DisplayName("Throws exception if user does not exist")
    public void whenUserDoesNotExistThenThrowException() {
        when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationValidator.validateUser(NON_EXISTING_USER_ID));
    }

    @Test
    @DisplayName("Successfully validates an existing user")
    public void whenUserExistsThenReturnUser() {
        User user = User.builder().id(USER_ID).build();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        recommendationValidator.validateUser(USER_ID);

        verify(userRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Error if the recommendation is given earlier than 6 months later")
    public void whenValidateDateWithShortInternalThenException() {
        recommendation.setCreatedAt(LocalDateTime.now());
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                requestRecommendationDto.getAuthorId(), requestRecommendationDto.getReceiverId()))
                .thenReturn(Optional.of(recommendation));

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendation(requestRecommendationDto));
    }

    @Test
    @DisplayName("Success if the recommendation is given after more than 6 months")
    public void whenValidateDateWithNormalInternalThenSuccess() {
        recommendation.setCreatedAt(LAST_RECOMMENDATION_DATE);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                requestRecommendationDto.getAuthorId(), requestRecommendationDto.getReceiverId()))
                .thenReturn(Optional.of(recommendation));
        when(skillRepository.existsByTitle(EXISTING_SKILL_TITLE)).thenReturn(true);
        when(skillRepository.existsByTitle(NON_EXISTING_SKILL_TITLE)).thenReturn(true);

        recommendationValidator.validateRecommendation(requestRecommendationDto);

        verify(recommendationRepository)
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(requestRecommendationDto.getAuthorId(),
                        requestRecommendationDto.getReceiverId());
    }

    @Test
    @DisplayName("Throws exception if skill offers list is null")
    public void whenSkillOffersListIsNullThenThrowException() {
        requestRecommendationDto.setSkillOffers(null);

        assertThrows(NoSuchElementException.class,
                () -> recommendationValidator.validateRecommendation(requestRecommendationDto));
    }

    @Test
    @DisplayName("Throws exception if skill offers list is empty")
    public void whenSkillOffersListIsEmptyThenThrowException() {
        requestRecommendationDto.setSkillOffers(List.of());

        assertThrows(NoSuchElementException.class,
                () -> recommendationValidator.validateRecommendation(requestRecommendationDto));
    }

    @Test
    @DisplayName("No exception thrown if skill offers list is not empty")
    public void whenSkillOffersListIsNotEmptyThenNoException() {
        requestRecommendationDto.setSkillOffers(List.of(
                SkillOfferDto.builder().skillTitle(EXISTING_SKILL_TITLE).build()
        ));

        when(skillRepository.existsByTitle(EXISTING_SKILL_TITLE)).thenReturn(true);

        recommendationValidator.validateRecommendation(requestRecommendationDto);

        verify(skillRepository).existsByTitle(EXISTING_SKILL_TITLE);
    }

    @Test
    @DisplayName("Throws exception if a skill title does not exist")
    public void whenSkillTitleDoesNotExistThenThrowException() {
        when(skillRepository.existsByTitle(EXISTING_SKILL_TITLE)).thenReturn(true);
        when(skillRepository.existsByTitle(NON_EXISTING_SKILL_TITLE)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendation(requestRecommendationDto));
    }

    @Test
    @DisplayName("Successfully validates if all skill titles exist")
    public void whenAllSkillTitlesExistThenValidationSucceeds() {
        when(skillRepository.existsByTitle(EXISTING_SKILL_TITLE)).thenReturn(true);
        when(skillRepository.existsByTitle(NON_EXISTING_SKILL_TITLE)).thenReturn(true);

        recommendationValidator.validateRecommendation(requestRecommendationDto);

        verify(skillRepository).existsByTitle(EXISTING_SKILL_TITLE);
        verify(skillRepository).existsByTitle(NON_EXISTING_SKILL_TITLE);
    }
}
