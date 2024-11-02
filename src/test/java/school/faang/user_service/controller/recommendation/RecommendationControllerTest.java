package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {
    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    void testGiveRecommendationWithNullContent() {
        testThrowsWithInvalidField("content", "NotNull", "Content must not be null");
    }

    @Test
    void testGiveRecommendationWithBlankContent() {
        testThrowsWithInvalidField("content", "NotBlank", "Content must not be blank");
    }

    @Test
    void testGiveRecommendationWithNullAuthorId() {
        testThrowsWithInvalidField("authorId", "NotNull", "Author ID must not be null");
    }

    @Test
    void testGiveRecommendationWithNullReceiverId() {
        testThrowsWithInvalidField("receiverId", "NotNull", "Receiver ID must not be null");
    }

    @Test
    void testGiveRecommendationWithNullSkillOffers() {
        testThrowsWithInvalidField("skillOffers", "NotNull", "Skill offers must not be null");
    }

    @Test
    void testGiveRecommendationWithBlankSkillOffers() {
        testThrowsWithInvalidField("skillOffers", "NotBlank", "Skill offers must not be empty");
    }

    private void testThrowsWithInvalidField(String fieldName, String errorCode, String message) {
        RecommendationDto recommendationDto = RecommendationDto.builder().build();
        BindingResult bindingResult = new BeanPropertyBindingResult(recommendationDto, "recommendationDto");
        bindingResult.rejectValue(fieldName, errorCode, message);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationController.giveRecommendation(recommendationDto, bindingResult));
        assertEquals(message, exception.getMessage());
    }
}