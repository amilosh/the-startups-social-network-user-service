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
    @DisplayName("Test Exception throw when Author Id is Null")
    void testAuthorIdIsNull() {
        dto.setAuthorId(null);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Author Id is 0")
    void testAuthorIdIsZero() {
        dto.setAuthorId(0L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Author Id is negative")
    void testAuthorIdIsNegative() {
        dto.setAuthorId(-5L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Receiver Id is Null")
    void testReceiverIdIsNull() {
        dto.setReceiverId(null);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Receiver Id is 0")
    void testReceiverIdIsZero() {
        dto.setReceiverId(0L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Receiver Id is negative")
    void testReceiverIdIsNegative() {
        dto.setReceiverId(-5L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Receiver and Author are same person")
    void testReceiverIdEqualsAuthorId() {
        dto.setReceiverId(3L);
        dto.setAuthorId(3L);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Content is Null")
    void testContentIsNull() {
        dto.setContent(null);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Content is Blank")
    void testContentIsBlank() {
        dto.setContent(" ");

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Skill Offer list is Null")
    void testSkillOfferListIsNull() {
        dto.setSkillOffers(null);

        assertThrows(DataValidationException.class, () -> recommendationControllerValidator.validateDto(dto));
    }

    @Test
    @DisplayName("Test Exception throw when Recommendation Id is Null")
    void testRecommendationIdIsNull() {
        dto.setId(null);

        assertThrows(DataValidationException.class, () ->
                recommendationControllerValidator.validateRecommendationId(dto.getId()));
    }

    @Test
    @DisplayName("Test Exception throw when Recommendation Id is 0")
    void testRecommendationIdIsZero() {
        dto.setId(0L);

        assertThrows(DataValidationException.class, () ->
                recommendationControllerValidator.validateRecommendationId(dto.getId()));
    }

    @Test
    @DisplayName("Test Exception throw when Recommendation Id is negative")
    void testRecommendationIdIsNegative() {
        dto.setId(-6L);

        assertThrows(DataValidationException.class, () ->
                recommendationControllerValidator.validateRecommendationId(dto.getId()));
    }
}
