package school.faang.user_service.service.validation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RecommendationValidationTest {
    private static final int MONTHS_BEFORE_NEW_RECOMMENDATION = 6;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @InjectMocks
    private RecommendationValidation recommendationValidation;

    @Test
    void testCheckIdNullRecommendationId() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationValidation.checkId(RecommendationDto.builder().build()));
        assertEquals("Null recommendation id", exception.getMessage());
    }

    @Test
    void testCheckIdNonExistentId() {
        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationValidation.checkId(RecommendationDto.builder().id(1L).build()));
        assertEquals("Recommendation not found", exception.getMessage());
    }

    @Test
    void testCheckIdCorrect() {
        long id = 1L;
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(Recommendation.builder().build()));
        recommendationValidation.checkId(RecommendationDto.builder().id(id).build());
    }

    @Test
    void testCheckTimeIntervalWithNoLastRecommendationFound() {
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        recommendationValidation.checkTimeInterval(RecommendationDto.builder().authorId(1L).receiverId(2L).build());
    }

    @Test
    void testCheckTimeIntervalWithLastRecommendationTooRecent() {
        LocalDateTime lastCreated = LocalDateTime.now();
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(
                        Recommendation.builder().createdAt(lastCreated).build()));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationValidation
                        .checkTimeInterval(RecommendationDto.builder().authorId(1L).receiverId(2L).build()));
        assertEquals("Must pass " + MONTHS_BEFORE_NEW_RECOMMENDATION
                + " before new recommendation for the same user", exception.getMessage());
    }

    @Test
    void testCheckTimeIntervalWithCorrect() {
        LocalDateTime lastCreated = LocalDateTime.now().minusMonths(MONTHS_BEFORE_NEW_RECOMMENDATION);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(
                        Recommendation.builder().createdAt(lastCreated).build()));
        recommendationValidation
                .checkTimeInterval(RecommendationDto.builder().authorId(1L).receiverId(2L).build());
    }

    @Test
    void testCheckSkillsExistWithNonExistent() {
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(1L).build(),
                SkillOfferDto.builder().skillId(2L).build()
        );
        when(skillRepository.countExisting(skillOffers.stream().mapToLong(SkillOfferDto::getSkillId).boxed().toList()))
                .thenReturn(skillOffers.size() - 1);
        assertThrows(DataValidationException.class, () -> recommendationValidation
                .checkSkillsExist(RecommendationDto.builder().skillOffers(skillOffers).build()));
    }

    @Test
    void testCheckSkillsExistWithAllExist() {
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(1L).build(),
                SkillOfferDto.builder().skillId(2L).build()
        );
        when(skillRepository.countExisting(skillOffers.stream().mapToLong(SkillOfferDto::getSkillId).boxed().toList()))
                .thenReturn(skillOffers.size());
        recommendationValidation.checkSkillsExist(RecommendationDto.builder().skillOffers(skillOffers).build());
    }

    @Test
    void testCheckSkillsUniqueWithNotUnique() {
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(1L).build(),
                SkillOfferDto.builder().skillId(1L).build()
        );
        DataValidationException exception = assertThrows(DataValidationException.class, () -> recommendationValidation
                .checkSkillsUnique(RecommendationDto.builder().skillOffers(skillOffers).build()));
        assertEquals("Skills must be unique", exception.getMessage());
    }

    @Test
    void testCheckSkillsUniqueWithCorrect() {
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(1L).build(),
                SkillOfferDto.builder().skillId(2L).build()
        );
        recommendationValidation.checkSkillsUnique(RecommendationDto.builder().skillOffers(skillOffers).build());
    }

    @Test
    void testCheckRequestWithNotFound() {
        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationValidation.checkRequest(RecommendationDto.builder().requestId(1L).build()));
        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void testCheckRequestWithAlreadyProcessed() {
        long requestId = 1L;
        Optional<RecommendationRequest> optional = Optional.of(
                RecommendationRequest.builder()
                        .status(RequestStatus.ACCEPTED)
                        .build()
        );
        when(recommendationRequestRepository.findById(requestId))
                .thenReturn(optional);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationValidation.checkRequest(RecommendationDto.builder().requestId(requestId).build()));
        assertEquals("Request already processed", exception.getMessage());
    }

    @Test
    void testCheckRequestWithCorrect() {
        long requestId = 1L;
        Optional<RecommendationRequest> optional = Optional.of(
                RecommendationRequest.builder()
                        .status(RequestStatus.PENDING)
                        .build()
        );
        when(recommendationRequestRepository.findById(requestId))
                .thenReturn(optional);
        recommendationValidation.checkRequest(RecommendationDto.builder().requestId(requestId).build());
    }
}