package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class RecommendationRequestValidatorTest {

    private RecommendationRequestValidator validator;
    private RecommendationRequestRepository recommendationRequestRepository;
    private SkillValidator skillValidator;

    @BeforeEach
    void setUp() {
        recommendationRequestRepository = mock(RecommendationRequestRepository.class);
        skillValidator = mock(SkillValidator.class);
        validator = new RecommendationRequestValidator(recommendationRequestRepository, skillValidator);
    }

    @Test
    void testValidateUsersExistence_BothUsersExist() {
        User requester = new User();
        User receiver = new User();
        assertDoesNotThrow(() -> validator.validateUsersExistence(requester, receiver));
    }

    @Test
    void testValidateUsersExistence_RequesterIsNull() {
        User receiver = new User();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUsersExistence(null, receiver)
        );
        assertEquals("Пользователя, запрашивающего рекомендацию не существует", exception.getMessage());
    }

    @Test
    void testValidateUsersExistence_ReceiverIsNull() {
        User requester = new User();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUsersExistence(requester, null)
        );
        assertEquals("Пользователя, получающего рекомендацию не существует", exception.getMessage());
    }

    @Test
    void testValidateSkillsExistence_SkillsExist() {
        List<Long> skillIds = Arrays.asList(1L, 2L);
        doNothing().when(skillValidator).validateSkills(skillIds);
        assertDoesNotThrow(() -> validator.validateSkillsExistence(skillIds));
        verify(skillValidator, times(1)).validateSkills(skillIds);
    }

    @Test
    void testValidateSkillsExistence_SkillsNull() {
        assertDoesNotThrow(() -> validator.validateSkillsExistence(null));
        verifyNoInteractions(skillValidator);
    }

    @Test
    void testValidateSkillsExistence_SkillsEmpty() {
        assertDoesNotThrow(() -> validator.validateSkillsExistence(Arrays.asList()));
        verifyNoInteractions(skillValidator);
    }

    @Test
    void testValidateRejectRequest_RequestNotAcceptedOrRejected() {
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.PENDING);
        assertDoesNotThrow(() -> validator.validateRejectRequest(request));
    }

    @Test
    void testValidateRejectRequest_RequestAlreadyAccepted() {
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.ACCEPTED);
        Exception exception = assertThrows(IllegalStateException.class, () ->
                validator.validateRejectRequest(request)
        );
        assertEquals("Невозможно отклонить запрос на рекомендацию, поскольку он уже имеет статус ACCEPTED", exception.getMessage());
    }

    @Test
    void testValidateRejectRequest_RequestAlreadyRejected() {
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.REJECTED);
        Exception exception = assertThrows(IllegalStateException.class, () ->
                validator.validateRejectRequest(request)
        );
        assertEquals("Невозможно отклонить запрос на рекомендацию, поскольку он уже имеет статус REJECTED", exception.getMessage());
    }

    @Test
    void testValidateRequestFrequency_NoPreviousRequest() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.empty());
        assertDoesNotThrow(() -> validator.validateRequestFrequency(requesterId, receiverId));
    }

    @Test
    void testValidateRequestFrequency_LastRequestLessThanSixMonthsAgo() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        RecommendationRequest lastRequest = new RecommendationRequest();
        lastRequest.setCreatedAt(LocalDateTime.now().minusMonths(3));
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(lastRequest));
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateRequestFrequency(requesterId, receiverId)
        );
        assertEquals("Запрос этому пользователю можно отправлять только раз в полгода", exception.getMessage());
    }

}
