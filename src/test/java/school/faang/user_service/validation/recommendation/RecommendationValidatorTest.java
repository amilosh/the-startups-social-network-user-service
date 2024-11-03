package school.faang.user_service.validation.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationValidatorTest {
    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillValidation skillValidation;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    private RecommendationDto dto;
    private Recommendation recommendation;


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
    }

    @Test
    @DisplayName("Test Exception throw when Author Id is Null")
    void testAuthorIdIsNull() {
        dto.setAuthorId(null);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Author Id is 0")
    void testAuthorIdIsZero() {
        dto.setAuthorId(0L);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Author Id is negative")
    void testAuthorIdIsNegative() {
        dto.setAuthorId(-5L);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Receiver Id is Null")
    void testReceiverIdIsNull() {
        dto.setReceiverId(null);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Receiver Id is 0")
    void testReceiverIdIsZero() {
        dto.setReceiverId(0L);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Receiver Id is negative")
    void testReceiverIdIsNegative() {
        dto.setReceiverId(-5L);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Receiver and Author are same person")
    void testReceiverIdEqualsAuthorId() {
        dto.setReceiverId(3L);
        dto.setAuthorId(3L);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Content is Null")
    void testContentIsNull() {
        dto.setContent(null);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Content is Blank")
    void testContentIsBlank() {
        dto.setContent(" ");

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Skill Offer list is Null")
    void testSkillOfferListIsNull() {
        dto.setSkillOffers(null);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Recommendation Id is Null")
    void testRecommendationIdIsNull() {
        dto.setId(null);

        assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateId(dto.getId()));
    }

    @Test
    @DisplayName("Test Exception throw when Id is 0")
    void testIdIsZero() {
        dto.setId(0L);

        assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateId(dto.getId()));
    }

    @Test
    @DisplayName("Test Exception throw when Id is negative")
    void testIdIsNegative() {
        dto.setId(-6L);

        assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateId(dto.getId()));
    }

    @Test
    void testRecommendationWithEarlyDate() {
        User author = new User();
        author.setId(dto.getAuthorId());
        recommendation.setAuthor(author);
        recommendation.setCreatedAt(LocalDateTime.now().minusDays(150));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateTimeAfterLastRecommendation(dto));
    }

    @Test
    void testSkillNotExists() {
        when(skillValidation.validateSkillExists(Mockito.anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateSkillExists(dto));
    }

    @Test
    void testRecommendationExistenceInvalidId() {
        recommendation.setId(1L);
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(false);

        Exception result = assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateRecommendationExistsById(recommendation.getId()));

        assertEquals("Recommendation with id #" + recommendation.getId() + " doesn't exist in the system",
                result.getMessage());
    }
}
