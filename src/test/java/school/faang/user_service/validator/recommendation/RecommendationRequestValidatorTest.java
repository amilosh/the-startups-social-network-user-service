package school.faang.user_service.validator.recommendation;

import com.sun.jdi.request.DuplicateRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.recommendation.RequestStatusException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long SKILL_ID = 1L;
    private static final long SKILL_REQUEST_ID = 1L;
    private static final long RECOMMENDATION_REQUEST_ID = 1L;
    private static final RequestStatus RECOMMENDATION_REQUEST_STATUS = RequestStatus.PENDING;
    private static final RequestStatus RECOMMENDATION_REQUEST_STATUS_REJECTED = RequestStatus.REJECTED;
    private static final String MESSAGE = "message";
    private static final String SKILL_TITLE = "Java";

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;

    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    public void setUp() {
        SkillRequestDto skillRequestDto = SkillRequestDto.builder()
                .id(SKILL_REQUEST_ID)
                .recommendationRequestId(RECOMMENDATION_REQUEST_ID)
                .skillId(SKILL_ID)
                .skillTitle(SKILL_TITLE)
                .build();

        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .message(MESSAGE)
                .status(RECOMMENDATION_REQUEST_STATUS)
                .skillRequests(List.of(skillRequestDto))
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .build();

        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusMonths(3));
    }

    @Test
    @DisplayName("Validate recommendation from DB - success")
    void testValidateRecommendationFromBdSuccess() {
        when(recommendationRequestRepository.findById(RECOMMENDATION_REQUEST_ID)).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequest result = recommendationRequestValidator.validateRecommendationFromBd(RECOMMENDATION_REQUEST_ID);

        assertEquals(recommendationRequest, result);
        verify(recommendationRequestRepository).findById(RECOMMENDATION_REQUEST_ID);
    }

    @Test
    @DisplayName("Validate recommendation from DB - not found")
    void testValidateRecommendationFromBdNotFound() {
        when(recommendationRequestRepository.findById(RECOMMENDATION_REQUEST_ID)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> recommendationRequestValidator.validateRecommendationFromBd(RECOMMENDATION_REQUEST_ID));

        assertEquals("There isn't recommendationRequest with id = " + RECOMMENDATION_REQUEST_ID, exception.getMessage());
        verify(recommendationRequestRepository).findById(RECOMMENDATION_REQUEST_ID);
    }

    @Test
    @DisplayName("Validate recommendation - success")
    void testValidateRecommendationSuccess() {
        when(recommendationRequestRepository.findLatestPendingRequest(REQUESTER_ID, RECEIVER_ID)).thenReturn(Optional.empty());
        when(skillRepository.existsByTitle(SKILL_TITLE)).thenReturn(true);

        assertDoesNotThrow(() -> recommendationRequestValidator.validateRecommendation(recommendationRequestDto));
    }

    @Test
    @DisplayName("Validate recommendation - duplicate request")
    void testValidateRecommendationDuplicateRequest() {
        when(recommendationRequestRepository.findLatestPendingRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(recommendationRequest));

        DuplicateRequestException exception = assertThrows(DuplicateRequestException.class,
                () -> recommendationRequestValidator.validateRecommendation(recommendationRequestDto));

        assertTrue(exception.getMessage().contains("There is already a pending request created less than"));
        verify(recommendationRequestRepository).findLatestPendingRequest(REQUESTER_ID, RECEIVER_ID);
    }

    @Test
    @DisplayName("Validate recommendation - skill not found")
    void testValidateRecommendationSkillNotFound() {
        when(recommendationRequestRepository.findLatestPendingRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.empty());
        when(skillRepository.existsByTitle(SKILL_TITLE))
                .thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateRecommendation(recommendationRequestDto)
        );

        assertEquals("SKILL WITH NAME " + SKILL_TITLE + " DOES NOT EXIST IN SYSTEM", exception.getMessage());
        verify(skillRepository).existsByTitle(SKILL_TITLE);
    }

    @Test
    @DisplayName("Check request status - request is not pending")
    void testCheckRequestStatusNotPending() {
        RequestStatusException exception = assertThrows(RequestStatusException.class, () ->
                recommendationRequestValidator
                        .checkRequestsStatus(RECOMMENDATION_REQUEST_ID, RECOMMENDATION_REQUEST_STATUS_REJECTED)
        );

        assertEquals("RECOMMENDATION REQUEST WITH ID " + RECOMMENDATION_REQUEST_ID + " HAS THE STATUS "
                + RECOMMENDATION_REQUEST_STATUS_REJECTED + ". OPERATION CANNOT BE PERFORMED", exception.getMessage());
    }

    @Test
    @DisplayName("Check request status - request is pending")
    void testCheckRequestStatusPending() {
        assertDoesNotThrow(() -> recommendationRequestValidator
                .checkRequestsStatus(RECOMMENDATION_REQUEST_ID, RECOMMENDATION_REQUEST_STATUS));
    }
}
