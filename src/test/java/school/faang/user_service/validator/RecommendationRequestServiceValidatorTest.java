package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceValidatorTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private RecommendationRequestServiceValidator recommendationRequestServiceValidator;

    @Test
    void testRequesterNotExistsInDatabase() {
        long requesterId = 1L;
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto()
                .setRequesterId(requesterId);
        when(userRepository.existsById(requesterId)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> recommendationRequestServiceValidator.validateExistsRequesterAndReceiverInDatabase(recommendationRequestDto));
    }

    @Test
    void testReceiverNotExistsInDatabase() {
        long requesterId = 1L;
        long receiverId = 2L;
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto()
                .setRequesterId(requesterId)
                .setReceiverId(receiverId);
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(userRepository.existsById(receiverId)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> recommendationRequestServiceValidator.validateExistsRequesterAndReceiverInDatabase(recommendationRequestDto));
    }

    @Test
    void testRequestLimitIsLessThanSixMonths() {
        Optional<RecommendationRequest> recommendationRequestReturned = Optional.of(new RecommendationRequest()
                .setCreatedAt(LocalDateTime.now()));
        long requesterId = 1L;
        long receiverId = 2L;
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto()
                .setRequesterId(requesterId)
                .setReceiverId(receiverId);
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(recommendationRequestReturned);

        assertThrows(DataValidationException.class,
                () -> recommendationRequestServiceValidator.validateSixMonthRequestLimit(recommendationRequestDto));
    }

    @Test
    void testSkillsNotExistenceInDatabase() {
        List<Long> skillIds = Arrays.asList(1L, 2L);
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto()
                .setSkillIds(skillIds);
        when(skillRepository.existsById(argThat(id -> Objects.equals(id, skillIds.get(0)) || Objects.equals(id, skillIds.get(1)))))
                .thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> recommendationRequestServiceValidator.validateExistsSkillsInDatabase(recommendationRequestDto));
    }
}