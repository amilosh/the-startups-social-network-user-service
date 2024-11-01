package school.faang.user_service.validation.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerValidatorTest {
    private RecommendationControllerValidator recommendationControllerValidator;
    private RecommendationDto dto;

    @BeforeEach
    void setUp() {
        recommendationControllerValidator = new RecommendationControllerValidator();
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
    @DisplayName("testAuthorIdIsNull")
    void testAuthorIdIsNull() {
        dto.setAuthorId(null);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testAuthorIdIsZero")
    void testAuthorIdIsZero() {
        dto.setAuthorId(0L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testAuthorIdIsNegative")
    void testAuthorIdIsNegative() {
        dto.setAuthorId(-5L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testReceiverIdIsNull")
    void testReceiverIdIsNull() {
        dto.setReceiverId(null);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testReceiverIdIsZero")
    void testReceiverIdIsZero() {
        dto.setReceiverId(0L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testReceiverIdIsNegative")
    void testReceiverIdIsNegative() {
        dto.setReceiverId(-5L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testReceiverIdEqualsAuthorId")
    void testReceiverIdEqualsAuthorId() {
        dto.setReceiverId(3L);
        dto.setAuthorId(3L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testContentIsNull")
    void testContentIsNull() {
        dto.setContent(null);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testContentIsBlank")
    void testContentIsBlank() {
        dto.setContent(" ");

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("testRecommendationIdIsNull")
    void testRecommendationIdIsNull() {
        dto.setId(null);

        assertThrows(DataValidationException.class, () ->
                recommendationControllerValidator.validateRecommendationId(dto.getId()));
    }

    @Test
    @DisplayName("testRecommendationIdIsZero")
    void testRecommendationIdIsZero() {
        dto.setId(0L);

        assertThrows(DataValidationException.class, () ->
                recommendationControllerValidator.validateRecommendationId(dto.getId()));
    }

    @Test
    @DisplayName("testRecommendationIdIsNegative")
    void testRecommendationIdIsNegative() {
        dto.setId(-6L);

        assertThrows(DataValidationException.class, () ->
                recommendationControllerValidator.validateRecommendationId(dto.getId()));
    }
}
