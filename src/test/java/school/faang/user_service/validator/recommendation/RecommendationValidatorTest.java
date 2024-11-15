package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private UserValidator userValidator;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptorFirst;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptorSecond;
    @InjectMocks
    private RecommendationValidator recommendationValidator;


    private final long authorId = 1L;
    private final long receiverId = 2L;
    private final long skillFirstId = 1L;
    private final long skillSecondId = 2L;
    private final long recommendationId = 1L;


    @Test
    public void validateContentWithContentNullFailTest() {
        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> recommendationValidator.validateContent(null)
                );

        assertEquals(RecommendationValidator.CONTENT_IS_EMPTY, dataValidationException.getMessage());
    }

    @Test
    public void validateContentWithBlankContentFailTest() {
        String content = " ";

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> recommendationValidator.validateContent(content)
                );

        assertEquals(RecommendationValidator.CONTENT_IS_EMPTY, dataValidationException.getMessage());
    }

    @Test
    public void validateContentWithValidContentSuccessTest() {
        RecommendationValidator recommendationValidatorMock = mock(RecommendationValidator.class);
        String content = "Content";

        doNothing().when(recommendationValidatorMock).validateContent(stringArgumentCaptor.capture());
        recommendationValidatorMock.validateContent(content);

        assertEquals(content, stringArgumentCaptor.getValue());
    }

    @Test
    public void validateAuthorExistSuccessTest() {
        recommendationValidator.validateAuthorExist(authorId);

        verify(userValidator, times(1)).existsAuthorById(longArgumentCaptorFirst.capture());
        assertEquals(authorId, longArgumentCaptorFirst.getValue());
    }

    @Test
    public void validateReceiverExistSuccessTest() {
        recommendationValidator.validateReceiverExist(receiverId);

        verify(userValidator, times(1)).existsReceiverById(longArgumentCaptorFirst.capture());
        assertEquals(receiverId, longArgumentCaptorFirst.getValue());
    }

    @Test
    public void validatePeriodWithNotFoundRecommendationResultSuccessTest() {
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.empty());

        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        verify(recommendationRepository, times(1)).
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(longArgumentCaptorFirst.capture(), longArgumentCaptorSecond.capture());
        assertEquals(authorId, longArgumentCaptorFirst.getValue());
        assertEquals(receiverId, longArgumentCaptorSecond.getValue());
    }

    @Test
    public void validatePeriodWithNotValidPeriodFailTest() {
        Recommendation recommendation = getRecommendationWithNotValidCreatedAt();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.of(recommendation));

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class,
                        () -> recommendationValidator.validatePeriod(authorId, receiverId)
                );
        assertEquals(String.format(RecommendationValidator.PERIOD_HAS_NOT_EXPIRED, authorId, receiverId),
                dataValidationException.getMessage());
    }

    @Test
    public void validatePeriodWithValidPeriodSuccessTest() {
        Recommendation recommendation = getRecommendationWithValidCreatedAt();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                longArgumentCaptorFirst.capture(),
                longArgumentCaptorSecond.capture()))
                .thenReturn(Optional.of(recommendation));

        recommendationValidator.validatePeriod(authorId, receiverId);

        assertEquals(authorId, longArgumentCaptorFirst.getValue());
        assertEquals(receiverId, longArgumentCaptorSecond.getValue());
        verify(recommendationRepository, times(1)).
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
    }

    @Test
    public void validateRecommendationExistWithExistRecommendationSuccessTest() {
        Long recommendationId = 1L;
        when(recommendationRepository.existsById(recommendationId)).thenReturn(true);

        recommendationValidator.validateRecommendationExist(recommendationId);

        verify(recommendationRepository, times(1)).
                existsById(longArgumentCaptorFirst.capture());
        assertEquals(recommendationId, longArgumentCaptorFirst.getValue());
    }


    @Test
    public void validateRecommendationExistWithNotExistRecommendationSuccessTest() {
        when(recommendationRepository.existsById(recommendationId)).thenReturn(false);

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class,
                        () -> recommendationValidator.validateRecommendationExist(recommendationId)
                );

        assertEquals(String.format(RecommendationValidator.RECOMMENDATION_NOT_EXIST_BY_ID, recommendationId), dataValidationException.getMessage());
        verify(recommendationRepository, times(1)).
                existsById(longArgumentCaptorFirst.capture());
        assertEquals(recommendationId, longArgumentCaptorFirst.getValue());
    }

    @Test
    public void validateSkillsWithNotExistSkillSecondIdFailTest() {
        Long skillFirstId = 1L;
        Long skillSecondId = 2L;
        Stream<Long> skillIds = Stream.of(skillFirstId, skillSecondId);
        when(skillValidator.existsById(skillFirstId)).thenReturn(true);
        when(skillValidator.existsById(skillSecondId)).thenReturn(false);

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class,
                        () -> recommendationValidator.validateSkills(skillIds)
                );

        assertEquals(String.format(RecommendationValidator.SKILL_DOES_NOT_EXIST, skillSecondId), dataValidationException.getMessage());
        verify(skillValidator, times(1)).existsById(skillFirstId);
        verify(skillValidator, times(1)).existsById(skillSecondId);
    }

    @Test
    public void validateSkillsWithExistIdsSuccessTest() {
        Stream<Long> skillIds = Stream.of(skillFirstId, skillSecondId);

        when(skillValidator.existsById(skillFirstId)).thenReturn(true);
        when(skillValidator.existsById(skillSecondId)).thenReturn(true);

        recommendationValidator.validateSkills(skillIds);

        verify(skillValidator, times(1)).existsById(skillFirstId);
        verify(skillValidator, times(1)).existsById(skillSecondId);
    }

    @Test
    public void validateRecommendationExistWithRecommendationNotFoundSuccessTest() {
        RecommendationDto recommendationDto = getRecommendationDtoWithNotValidCreatedAt();

        when(recommendationRepository.existsById(recommendationId)).thenReturn(true);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                longArgumentCaptorFirst.capture(),
                longArgumentCaptorSecond.capture()))
                .thenReturn(Optional.empty());

        recommendationValidator.validateRecommendationExist(recommendationDto);

        assertEquals(authorId, longArgumentCaptorFirst.getValue());
        assertEquals(receiverId, longArgumentCaptorSecond.getValue());

        verify(recommendationRepository, times(1)).
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
    }

    @Test
    public void validateRecommendationExistWithRecommendationFoundFailTest() {
        RecommendationDto recommendationDto = getRecommendationDtoWithNotValidCreatedAt();
        Recommendation recommendation = getRecommendationWithValidCreatedAt();

        when(recommendationRepository.existsById(recommendationId)).thenReturn(true);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                longArgumentCaptorFirst.capture(),
                longArgumentCaptorSecond.capture()))
                .thenReturn(Optional.of(recommendation));

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendationExist(recommendationDto));

        assertEquals(String.format(RecommendationValidator.RECOMMENDATION_NOT_EXIST, recommendationDto.getAuthorId(), recommendationDto.getReceiverId()), dataValidationException.getMessage());
        assertEquals(authorId, longArgumentCaptorFirst.getValue());
        assertEquals(receiverId, longArgumentCaptorSecond.getValue());
        verify(recommendationRepository, times(1)).
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
    }

    private Recommendation getRecommendationWithValidCreatedAt() {
        return Recommendation.builder()
                .createdAt(LocalDateTime.now().minusMonths(RecommendationValidator.MONTH_PERIOD + 1))
                .build();
    }

    private Recommendation getRecommendationWithNotValidCreatedAt() {
        return Recommendation.builder()
                .createdAt(LocalDateTime.now().minusMonths(RecommendationValidator.MONTH_PERIOD - 1))
                .build();
    }

    private RecommendationDto getRecommendationDtoWithNotValidCreatedAt() {
        Long recommendationId = 1L;
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(recommendationId);
        recommendationDto.setAuthorId(authorId);
        recommendationDto.setReceiverId(receiverId);
        recommendationDto.setSkillOffers(List.of(
                new SkillOfferDto(3L, skillFirstId, recommendationId),
                new SkillOfferDto(4L, skillSecondId, recommendationId)
        ));
        return recommendationDto;
    }
}

